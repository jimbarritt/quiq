use serde::Deserialize;
use std::path::Path;

const DEFAULT_PATH: &str = "build/reports/tests/acceptance/cucumber/cucumber.json";

#[derive(Deserialize)]
struct Feature {
    name: String,
    #[serde(default)]
    elements: Vec<Scenario>,
}

#[derive(Deserialize)]
struct Scenario {
    name: String,
    #[serde(default)]
    steps: Vec<Step>,
}

#[derive(Deserialize)]
struct Step {
    keyword: String,
    name: String,
    result: Option<StepResult>,
}

#[derive(Deserialize)]
struct StepResult {
    status: String,
    error_message: Option<String>,
}

pub fn run(path: Option<&str>) {
    let input_path = path.unwrap_or(DEFAULT_PATH);

    if !Path::new(input_path).exists() {
        eprintln!("quiq: could not find cucumber JSON at: {}", input_path);
        eprintln!();
        eprintln!("To generate this file, add the following to your Gradle test task:");
        eprintln!();
        eprintln!("  tasks.named<Test>(\"acceptanceCuke\") {{");
        eprintln!("      systemProperty(");
        eprintln!("          \"cucumber.plugin\",");
        eprintln!("          \"json:{}\"", DEFAULT_PATH);
        eprintln!("      )");
        eprintln!("  }}");
        eprintln!();
        eprintln!("Then run: ./gradlew acceptanceCuke");
        std::process::exit(1);
    }

    let json = std::fs::read_to_string(input_path)
        .unwrap_or_else(|e| { eprintln!("quiq: failed to read {}: {}", input_path, e); std::process::exit(1); });

    let features: Vec<Feature> = serde_json::from_str(&json)
        .unwrap_or_else(|e| { eprintln!("quiq: failed to parse cucumber JSON: {}", e); std::process::exit(1); });

    let html = render(&features);

    let output_path = Path::new(input_path)
        .parent()
        .unwrap_or(Path::new("."))
        .join("quiq-report.html");

    std::fs::write(&output_path, html)
        .unwrap_or_else(|e| { eprintln!("quiq: failed to write report: {}", e); std::process::exit(1); });

    println!("quiq report: file://{}", output_path.canonicalize().unwrap().display());
}

fn render(features: &[Feature]) -> String {
    let all_scenarios: Vec<&Scenario> = features.iter().flat_map(|f| f.elements.iter()).collect();
    let total = all_scenarios.len();
    let passed = all_scenarios.iter().filter(|s| scenario_passed(s)).count();
    let failed = total - passed;
    let timestamp = chrono_now();

    let features_html: String = features.iter().map(render_feature).collect();

    format!(r#"<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Acceptance Tests</title>
  <style>
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{ font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; font-size: 14px; color: #1a1a1a; background: #fff; padding: 40px; max-width: 800px; margin: 0 auto; }}

    .header {{ margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: baseline; }}
    .header h1 {{ font-size: 14px; font-weight: 400; color: #444; }}
    .meta {{ font-size: 14px; color: #888; }}

    h2 {{ font-size: 14px; font-weight: 600; margin-bottom: 8px; margin-top: 24px; }}
    .feature {{ margin-bottom: 8px; }}

    details.scenario {{ margin-bottom: 6px; padding-left: 16px; border-left: 2px solid #eee; }}
    details.scenario.failed {{ border-left-color: #c88; }}
    details.scenario summary {{ font-size: 15px; font-weight: 500; padding: 3px 0; cursor: pointer; display: flex; align-items: center; gap: 6px; list-style: none; }}
    details.scenario summary::-webkit-details-marker {{ display: none; }}
    details.scenario summary::before {{ content: '▶'; font-size: 9px; color: #bbb; width: 12px; flex-shrink: 0; }}
    details.scenario[open] summary::before {{ transform: rotate(90deg); display: inline-block; }}

    .steps {{ list-style: none; padding-left: 16px; margin-top: 4px; margin-bottom: 6px; }}
    .step {{ padding: 2px 0; font-size: 14px; color: #444; display: flex; align-items: baseline; gap: 6px; flex-wrap: wrap; }}
    .step.skipped {{ color: #aaa; }}
    .keyword {{ color: #999; font-weight: 400; }}

    .icon {{ font-size: 11px; width: 14px; display: inline-block; flex-shrink: 0; }}
    .icon.pass {{ color: #5a9e6f; }}
    .icon.fail {{ color: #b94040; }}
    .icon.skip {{ color: #ccc; }}

    .error {{ font-size: 13px; color: #b94040; background: #fdf0f0; padding: 8px 12px; border-radius: 3px; margin-top: 6px; width: 100%; font-family: monospace; white-space: pre-wrap; }}
  </style>
</head>
<body>
  <div class="header">
    <h1>Acceptance Tests</h1>
    <div class="meta">Total {} - Passed {} - Failed {} @ {}</div>
  </div>
  {}
</body>
</html>"#,
        total, passed, failed, timestamp, features_html)
}

fn render_feature(feature: &Feature) -> String {
    let scenarios: String = feature.elements.iter().map(render_scenario).collect();
    format!(r#"
    <div class="feature">
      <h2>{}</h2>
      {}
    </div>"#,
        escape(&feature.name), scenarios)
}

fn render_scenario(scenario: &Scenario) -> String {
    let passed = scenario_passed(scenario);
    let status_class = if passed { "passed" } else { "failed" };
    let open_attr = if passed { "" } else { "open" };
    let steps: String = scenario.steps.iter().map(render_step).collect();

    format!(r#"
      <details class="scenario {}" {}>
        <summary>{} {}</summary>
        <ul class="steps">{}</ul>
      </details>"#,
        status_class, open_attr,
        status_icon(if passed { "passed" } else { "failed" }),
        escape(&scenario.name),
        steps)
}

fn render_step(step: &Step) -> String {
    let status = step.result.as_ref().map(|r| r.status.as_str()).unwrap_or("unknown");
    let error = step.result.as_ref()
        .and_then(|r| r.error_message.as_deref())
        .map(render_error)
        .unwrap_or_default();

    format!(r#"
        <li class="step {}">
          {}
          <span class="keyword">{}</span> {}
          {}
        </li>"#,
        status,
        status_icon(status),
        escape(step.keyword.trim()),
        escape(&step.name),
        error)
}

fn render_error(msg: &str) -> String {
    let filtered: Vec<&str> = msg.lines()
        .filter(|l| !l.trim().is_empty() && !l.trim().starts_with("at "))
        .collect();
    format!(r#"<div class="error">{}</div>"#, escape(&filtered.join("\n")))
}

fn status_icon(status: &str) -> &'static str {
    match status {
        "passed" => r#"<span class="icon pass">✓</span>"#,
        "failed" => r#"<span class="icon fail">✗</span>"#,
        _        => r#"<span class="icon skip">○</span>"#,
    }
}

fn scenario_passed(scenario: &Scenario) -> bool {
    scenario.steps.iter().all(|s| {
        s.result.as_ref().map(|r| r.status == "passed").unwrap_or(false)
    })
}

fn escape(s: &str) -> String {
    s.replace('&', "&amp;").replace('<', "&lt;").replace('>', "&gt;")
}

fn chrono_now() -> String {
    // Simple wall-clock timestamp without pulling in chrono
    use std::time::{SystemTime, UNIX_EPOCH};
    let secs = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs();
    let (y, mo, d, h, mi, s) = epoch_to_datetime(secs);
    format!("{:04}-{:02}-{:02} {:02}:{:02}:{:02}", y, mo, d, h, mi, s)
}

fn epoch_to_datetime(secs: u64) -> (u64, u64, u64, u64, u64, u64) {
    let s = secs % 60;
    let mins = secs / 60;
    let mi = mins % 60;
    let hours = mins / 60;
    let h = hours % 24;
    let days = hours / 24;
    // Approximate calendar (good enough for a timestamp)
    let year = 1970 + days / 365;
    let day_of_year = days % 365;
    let month_days = [31u64,28,31,30,31,30,31,31,30,31,30,31];
    let mut mo = 1u64;
    let mut remaining = day_of_year;
    for &md in &month_days {
        if remaining < md { break; }
        remaining -= md;
        mo += 1;
    }
    (year, mo, remaining + 1, h, mi, s)
}
