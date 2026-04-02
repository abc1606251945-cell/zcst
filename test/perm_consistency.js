const fs = require("fs");
const path = require("path");

const sqlPath = "d:/zcst/zcst-b/sql/zcst.sql";
const backendRoot = "d:/zcst/zcst-b";

function listFiles(dir, predicate) {
  const out = [];
  const stack = [dir];
  while (stack.length) {
    const cur = stack.pop();
    let entries = [];
    try {
      entries = fs.readdirSync(cur, { withFileTypes: true });
    } catch {
      continue;
    }
    for (const e of entries) {
      const p = path.join(cur, e.name);
      if (e.isDirectory()) {
        if (e.name === "target" || e.name === ".git" || e.name === "node_modules") continue;
        stack.push(p);
      } else if (e.isFile()) {
        if (!predicate || predicate(p)) out.push(p);
      }
    }
  }
  return out;
}

function extractInsert(sql, table) {
  const marker = `INSERT INTO \`${table}\` VALUES`;
  const i = sql.indexOf(marker);
  if (i < 0) return "";
  const j = sql.indexOf(";", i);
  if (j < 0) return "";
  return sql.slice(i + marker.length, j);
}

function parseTuples(values) {
  const tuples = [];
  let i = 0;
  while (i < values.length) {
    while (i < values.length && values[i] !== "(") i++;
    if (i >= values.length) break;
    i++;
    const row = [];
    let token = "";
    let inStr = false;
    let wasStr = false;
    for (; i < values.length; i++) {
      const ch = values[i];
      if (inStr) {
        if (ch === "'") {
          if (values[i + 1] === "'") {
            token += "'";
            i++;
          } else {
            inStr = false;
            wasStr = true;
          }
        } else {
          token += ch;
        }
        continue;
      }
      if (ch === "'") {
        inStr = true;
        continue;
      }
      if (ch === ",") {
        row.push(wasStr ? token : token.trim() === "" ? null : token.trim());
        token = "";
        wasStr = false;
        continue;
      }
      if (ch === ")") {
        row.push(wasStr ? token : token.trim() === "" ? null : token.trim());
        tuples.push(row);
        token = "";
        wasStr = false;
        i++;
        break;
      }
      token += ch;
    }
  }
  return tuples;
}

function splitPerms(perms) {
  if (!perms) return [];
  if (perms === "NULL") return [];
  return String(perms)
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean);
}

function loadSqlPerms() {
  const sql = fs.readFileSync(sqlPath, "utf8");
  const perms = new Set();
  for (const r of parseTuples(extractInsert(sql, "sys_menu"))) {
    const p = r[13];
    for (const item of splitPerms(p)) perms.add(item);
  }
  return perms;
}

function loadCodePerms() {
  const files = listFiles(backendRoot, (p) => p.endsWith(".java"));
  const perms = new Set();
  const re1 = /hasPermi\(\s*'([^']+)'\s*\)/g;
  const re2 = /hasPermi\(\s*\"([^\"]+)\"\s*\)/g;
  const re3 = /hasPermiOr\(\s*new\s+String\[\]\s*\{([\s\S]*?)\}\s*\)/g;
  for (const f of files) {
    let txt = "";
    try {
      txt = fs.readFileSync(f, "utf8");
    } catch {
      continue;
    }
    let m;
    while ((m = re1.exec(txt))) perms.add(m[1]);
    while ((m = re2.exec(txt))) perms.add(m[1]);
    while ((m = re3.exec(txt))) {
      const inner = m[1];
      const itemRe = /'([^']+)'/g;
      let im;
      while ((im = itemRe.exec(inner))) perms.add(im[1]);
    }
  }
  return perms;
}

function main() {
  const sqlPerms = loadSqlPerms();
  const codePerms = loadCodePerms();

  const missingInSql = [...codePerms].filter((p) => !sqlPerms.has(p)).sort();
  const unusedInCode = [...sqlPerms].filter((p) => !codePerms.has(p)).sort();

  console.log(`codePerms=${codePerms.size}`);
  console.log(`sqlPerms=${sqlPerms.size}`);
  console.log(`missingInSql=${missingInSql.length}`);
  if (missingInSql.length) {
    console.log("MISSING_IN_SQL (first 100):");
    for (const p of missingInSql.slice(0, 100)) console.log("-", p);
  }
  console.log(`\nunusedInCode=${unusedInCode.length}`);
  if (unusedInCode.length) {
    console.log("UNUSED_IN_CODE (first 100):");
    for (const p of unusedInCode.slice(0, 100)) console.log("-", p);
  }
}

main();

