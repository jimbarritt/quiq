use serde::Deserialize;
use std::path::Path;

const DEFAULT_INPUT: &str = "build/test-results/test";
const DEFAULT_OUTPUT: &str = "build/reports/tests/unit/quiq-report.html";

#[derive(Deserialize)]
struct TestSuite {
    #[serde(rename = "@name")]
    name: String,
    #[serde(rename = "testcase", default)]
    cases: Vec<TestCase>,
}

#[derive(Deserialize)]
struct TestCase {
    #[serde(rename = "@name")]
    name: String,
    #[serde(rename = "failure")]
    failure: Option<Failure>,
    #[serde(rename = "error")]
    error: Option<Failure>,
}

#[derive(Deserialize)]
struct Failure {
    #[serde(rename = "@message")]
    message: Option<String>,
}

pub fn run(path: Option<&str>) {
    let input_path = path.unwrap_or(DEFAULT_INPUT);

    if !Path::new(input_path).exists() {
        eprintln!("quiq: could not find JUnit XML results at: {}", input_path);
        eprintln!();
        eprintln!("To generate these files, run: ./gradlew test");
        eprintln!("Results will appear in: {}", DEFAULT_INPUT);
        std::process::exit(1);
    }

    let suites = load_suites(input_path);
    let html = render(&suites);

    let output_path = Path::new(DEFAULT_OUTPUT);
    std::fs::create_dir_all(output_path.parent().unwrap()).unwrap();
    std::fs::write(output_path, html)
        .unwrap_or_else(|e| { eprintln!("quiq: failed to write report: {}", e); std::process::exit(1); });

    println!("quiq report: file://{}", output_path.canonicalize().unwrap().display());
}

fn load_suites(dir: &str) -> Vec<TestSuite> {
    let mut suites = Vec::new();
    let entries = std::fs::read_dir(dir)
        .unwrap_or_else(|e| { eprintln!("quiq: failed to read {}: {}", dir, e); std::process::exit(1); });

    for entry in entries.flatten() {
        let path = entry.path();
        if path.extension().and_then(|e| e.to_str()) != Some("xml") { continue; }
        let xml = std::fs::read_to_string(&path).unwrap();
        match quick_xml::de::from_str::<TestSuite>(&xml) {
            Ok(suite) => suites.push(suite),
            Err(e) => eprintln!("quiq: skipping {:?}: {}", path.file_name().unwrap(), e),
        }
    }

    suites.sort_by(|a, b| a.name.cmp(&b.name));
    suites
}

fn render(suites: &[TestSuite]) -> String {
    let total: usize = suites.iter().map(|s| s.cases.len()).sum();
    let failed: usize = suites.iter().map(|s| s.cases.iter().filter(|c| c.failure.is_some() || c.error.is_some()).count()).sum();
    let passed = total - failed;
    let timestamp = chrono_now();

    let suites_html: String = suites.iter().map(render_suite).collect();

    format!(r#"<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Unit Tests</title>
  <style>
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{ font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; font-size: 14px; color: #1a1a1a; background: #fff; padding: 40px; max-width: 800px; margin: 0 auto; }}

    .header {{ margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: baseline; }}
    .header h1 {{ font-size: 14px; font-weight: 400; color: #444; }}
    .meta {{ font-size: 14px; color: #888; }}

    h2 {{ font-size: 14px; font-weight: 600; margin-bottom: 8px; margin-top: 24px; }}
    .suite {{ margin-bottom: 8px; }}

    .case {{ padding: 3px 0; display: flex; align-items: baseline; gap: 8px; padding-left: 16px; border-left: 2px solid #eee; margin-bottom: 4px; }}
    .case.failed {{ border-left-color: #c88; }}

    .icon {{ font-size: 11px; width: 14px; display: inline-block; flex-shrink: 0; }}
    .icon.pass {{ color: #5a9e6f; }}
    .icon.fail {{ color: #b94040; }}

    .case-name {{ font-size: 15px; font-weight: 500; }}
    .error {{ font-size: 13px; color: #b94040; background: #fdf0f0; padding: 8px 12px; border-radius: 3px; margin-top: 4px; margin-left: 22px; margin-bottom: 6px; font-family: monospace; white-space: pre-wrap; }}
  </style>
</head>
<body>
  <div class="header">
    <h1>Unit Tests</h1>
    <div class="meta">Total {} - Passed {} - Failed {} @ {}</div>
  </div>
  {}
</body>
</html>"#,
        total, passed, failed, timestamp, suites_html)
}

fn render_suite(suite: &TestSuite) -> String {
    let short_name = suite.name.rsplit('.').next().unwrap_or(&suite.name);
    let cases: String = suite.cases.iter().map(render_case).collect();
    format!(r#"
    <div class="suite">
      <h2>{}</h2>
      {}
    </div>"#,
        escape(short_name), cases)
}

fn render_case(case: &TestCase) -> String {
    let failure = case.failure.as_ref().or(case.error.as_ref());
    let status_class = if failure.is_some() { "failed" } else { "" };
    let icon = if failure.is_some() {
        r#"<span class="icon fail">✗</span>"#
    } else {
        r#"<span class="icon pass">✓</span>"#
    };
    let name = case.name.trim_end_matches("()");
    let error_html = failure
        .and_then(|f| f.message.as_deref())
        .map(|msg| format!(r#"<div class="error">{}</div>"#, escape(msg)))
        .unwrap_or_default();

    format!(r#"
      <div class="case {}">
        {} <span class="case-name">{}</span>
      </div>
      {}"#,
        status_class, icon, escape(name), error_html)
}

fn escape(s: &str) -> String {
    s.replace('&', "&amp;").replace('<', "&lt;").replace('>', "&gt;")
}

fn chrono_now() -> String {
    use std::time::{SystemTime, UNIX_EPOCH};
    let secs = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs();
    let (y, mo, d, h, mi, s) = epoch_to_datetime(secs);
    format!("{:04}-{:02}-{:02} {:02}:{:02}:{:02}", y, mo, d, h, mi, s)
}

fn epoch_to_datetime(secs: u64) -> (u64, u64, u64, u64, u64, u64) {
    let s = secs % 60;
    let mi = (secs / 60) % 60;
    let h = (secs / 3600) % 24;
    let days = secs / 86400;
    let year = 1970 + days / 365;
    let day_of_year = days % 365;
    let month_days = [31u64, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    let mut mo = 1u64;
    let mut remaining = day_of_year;
    for &md in &month_days {
        if remaining < md { break; }
        remaining -= md;
        mo += 1;
    }
    (year, mo, remaining + 1, h, mi, s)
}
