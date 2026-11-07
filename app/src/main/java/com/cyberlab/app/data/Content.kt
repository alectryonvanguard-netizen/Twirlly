package com.cyberlab.app.data

import android.util.Base64

private fun s(t: String, b: String) = Section(t, b)
private fun qn(q: String, a: Int, vararg o: String) = Question(q, o.toList(), a)
private fun les(id: String, t: String, m: Int, x: Int, secs: List<Section>, quiz: List<Question>) =
    Lesson(id, t, m, x, secs, quiz)

private const val FLAG4 = "flag{layered_encoding_is_not_encryption}"
private val MSG4: String = Base64.encodeToString(Terminal.rot13(FLAG4).toByteArray(), Base64.NO_WRAP)

object Content {

    fun lesson(id: String): Lesson = courses.flatMap { it.lessons }.first { it.id == id }
    fun courseOf(lessonId: String): Course = courses.first { c -> c.lessons.any { it.id == lessonId } }

    val courses: List<Course> = listOf(

        // ───────────── BEGINNER ─────────────
        Course("c1", "Cyber Foundations", Level.BEGINNER,
            "Ethics, law, threat actors and the models every security professional shares.",
            listOf(
                les("l1", "The Hacker Mindset and Ethics", 8, 20,
                    listOf(
                        s("Hat colors and the authorization line",
                            "White-hat testers work with written permission. Black-hat attackers act for gain or harm. Grey-hat researchers act without permission but without malice, which is still illegal in most places. The only thing separating a penetration tester from a criminal is authorization."),
                        s("Scope and rules of engagement",
                            "Before any test you need a signed scope: target IP ranges, domains, allowed techniques, testing windows, emergency contacts and data-handling rules. Anything outside the scope is off limits, even if it looks vulnerable. Stop and report if you find something unexpected, such as real customer data."),
                        s("The CIA triad and beyond",
                            "Confidentiality keeps data from unauthorized eyes, Integrity keeps it from unauthorized change, Availability keeps it reachable. Authenticity and non-repudiation extend the model. Every finding you report should name which property it breaks and what the business impact is.")
                    ),
                    listOf(
                        qn("What makes a security test legal and ethical?", 1,
                            "Using only free tools", "Written authorization and a defined scope", "Not causing any alerts", "Testing at night"),
                        qn("A flaw that lets anyone edit another user's account balance mainly breaks which property?", 1,
                            "Confidentiality", "Integrity", "Availability", "Anonymity")
                    )),
                les("l2", "Threat Actors and the Kill Chain", 10, 25,
                    listOf(
                        s("Who attacks",
                            "Opportunistic criminals chase quick money with ransomware and phishing. Hacktivists seek publicity. Insiders abuse legitimate access. Advanced persistent threat (APT) groups, often state-backed, run long, patient, targeted campaigns. Your threat model decides which defenses matter most."),
                        s("Kill chain and ATT&CK",
                            "The Cyber Kill Chain splits an intrusion into reconnaissance, weaponization, delivery, exploitation, installation, command and control, and actions on objectives. MITRE ATT&CK catalogs real adversary tactics and techniques, from Initial Access to Impact, and is the shared language of red and blue teams."),
                        s("Defense in depth",
                            "No single control stops everything. Layer network segmentation, patching, least privilege, MFA, logging and tested backups. Breaking the chain early costs the attacker the most, so detection at delivery and exploitation is worth far more than cleanup at the end.")
                    ),
                    listOf(
                        qn("Which framework catalogs real adversary tactics and techniques?", 1,
                            "CVSS", "MITRE ATT&CK", "OWASP Top 10", "NIST CSF"),
                        qn("Why use defense in depth?", 1,
                            "One strong control is enough", "Each layer can fail, so layers limit the blast radius",
                            "It replaces monitoring", "It removes the need for patching")
                    ))
            )),

        Course("c2", "Networking Essentials", Level.BEGINNER,
            "TCP/IP, DNS, HTTP, TLS and reading packets like an analyst.",
            listOf(
                les("l3", "TCP/IP, Ports and DNS", 12, 25,
                    listOf(
                        s("The stack",
                            "Data moves through link, internet, transport and application layers. IP handles addressing and routing, TCP gives reliable ordered delivery through a three-way handshake (SYN, SYN-ACK, ACK), and UDP trades reliability for speed. The OSI model gives seven layers to reason about where a fault or attack lives."),
                        s("Ports and services",
                            "A port identifies a service on a host: 22 SSH, 53 DNS, 80 HTTP, 443 HTTPS, 445 SMB, 3389 RDP. An open port is simply a listening service, and each one is attack surface. Good hygiene means exposing only what the business needs."),
                        s("DNS",
                            "DNS turns names into IP addresses using records such as A, AAAA, MX, TXT and CNAME. Attackers study DNS for subdomains and mail setup, and abuse it for spoofing and data tunneling. Defenders use DNSSEC, careful zone design and DNS logging.")
                    ),
                    listOf(
                        qn("Which sequence opens a TCP connection?", 0,
                            "SYN, SYN-ACK, ACK", "ACK, SYN, FIN", "HELLO, OK", "GET, POST, PUT"),
                        qn("Which port is HTTPS by default?", 1, "80", "443", "22", "53")
                    )),
                les("l4", "HTTP, TLS and Packet Analysis", 12, 25,
                    listOf(
                        s("HTTP basics",
                            "Requests carry a method, path, headers and sometimes a body. Responses return a status code: 2xx success, 3xx redirect, 4xx client error, 5xx server error. Cookies and headers like Authorization carry session state, so they are prime targets and need the Secure, HttpOnly and SameSite flags."),
                        s("TLS",
                            "TLS provides encryption, integrity and server authentication through certificates. Certificate validation, modern protocol versions and strong ciphers matter; expired or self-signed certificates and downgrades are classic findings. HSTS tells browsers to always use HTTPS."),
                        s("Reading packets",
                            "Wireshark and tcpdump capture traffic on networks you own or are authorized to monitor. Start with display filters such as ip.addr, tcp.port and http, follow a stream, and look for cleartext credentials, odd destinations and regular beaconing. Capturing traffic you are not authorized to see is illegal.")
                    ),
                    listOf(
                        qn("Which cookie flag stops JavaScript from reading a cookie?", 1,
                            "Secure", "HttpOnly", "SameSite", "Path"),
                        qn("An HTTP 403 status means", 2,
                            "Success", "Redirect", "Forbidden", "Server crash")
                    ))
            )),

        // ───────────── INTERMEDIATE ─────────────
        Course("c3", "Recon and OSINT", Level.INTERMEDIATE,
            "Map an attack surface from public data, then enumerate it actively and carefully.",
            listOf(
                les("l5", "Passive Recon and OSINT", 12, 30,
                    listOf(
                        s("Passive means no contact",
                            "Passive recon gathers information without touching the target's systems: WHOIS, DNS records, certificate transparency logs, search engines, code repositories, job posts and social media. It is quiet and uses public data, which makes it the safest first phase."),
                        s("What to look for",
                            "Subdomains, technology stacks, employee names and email formats, leaked credentials in public repositories, exposed cloud storage and old documents with metadata. Each item widens the map of the attack surface before a single packet is sent."),
                        s("Defend against it",
                            "Audit what you publish. Strip metadata from documents, scan repositories for secrets, monitor certificate logs for rogue subdomains and train staff on what not to share online.")
                    ),
                    listOf(
                        qn("Which is a passive recon source?", 1,
                            "Port scan", "Certificate transparency logs", "Brute-forcing logins", "Vulnerability scanner"),
                        qn("Best response to a secret leaked in a public repository?", 1,
                            "Delete the commit message", "Rotate the secret and enable secret scanning", "Rename the repo", "Disable HTTPS")
                    )),
                les("l6", "Active Scanning and Enumeration", 14, 35,
                    listOf(
                        s("Host and port discovery",
                            "Active recon sends traffic to the target. Host discovery finds live machines, then port scans find listening services. TCP connect, SYN and UDP scans differ in speed, reliability and how visible they are in logs. Only scan inside the authorized scope and window."),
                        s("Service enumeration",
                            "Version detection and banner grabbing identify software and versions, which you compare with known vulnerabilities. Enumerate shares, users, web paths and API endpoints carefully. Each result is a lead to verify, not proof of a vulnerability."),
                        s("Noise and detection",
                            "Scans are loud. Firewalls, IDS and rate limits flag them, which is exactly what defenders should see. Agree on how aggressive to be and tell the blue team, or deliberately test whether detection works.")
                    ),
                    listOf(
                        qn("Banner grabbing is used to", 1,
                            "Encrypt traffic", "Identify service software and versions", "Crack hashes", "Delete logs"),
                        qn("Before scanning a network you must", 1,
                            "Disable the firewall", "Confirm it is inside your written scope", "Spoof your address", "Scan the internet first")
                    ))
            )),

        Course("c4", "Web Application Security", Level.INTERMEDIATE,
            "OWASP Top 10 thinking, a repeatable testing method and reports that get fixed.",
            listOf(
                les("l7", "OWASP Top 10 Essentials", 15, 40,
                    listOf(
                        s("Broken access control",
                            "The most common serious flaw: users reach data or actions they should not, for example by changing an ID in a URL (IDOR). Fix with server-side authorization checks on every request, deny by default and never trust client-supplied identifiers."),
                        s("Injection and cryptographic failures",
                            "Injection happens when untrusted input is interpreted as code, as in SQL, OS commands or templates. Fix with parameterized queries, strict validation and least-privileged accounts. Cryptographic failures include weak hashing, missing TLS and hard-coded keys; use vetted libraries and slow password hashes such as Argon2 or bcrypt."),
                        s("Misconfiguration and vulnerable components",
                            "Default credentials, verbose errors, open admin panels and outdated libraries cause many breaches. Keep a dependency inventory, patch quickly, remove unused features and harden configurations through repeatable pipelines.")
                    ),
                    listOf(
                        qn("Best fix for SQL injection?", 1,
                            "Blacklist quote characters", "Parameterized queries", "Hide the database port", "Longer passwords"),
                        qn("IDOR is a form of", 0,
                            "Broken access control", "Weak TLS", "Denial of service", "Phishing")
                    )),
                les("l8", "Web Testing Methodology", 14, 40,
                    listOf(
                        s("Map the application",
                            "Crawl and note every route, parameter, role and API endpoint. Understand how authentication, sessions and file uploads work before testing anything. An intercepting proxy such as Burp Suite or OWASP ZAP records and replays requests so you can reason about each one."),
                        s("Test by category",
                            "Work through authentication, session management, authorization, input handling, business logic and client-side controls. Business logic flaws, like skipping a payment step, are invisible to scanners and often the most valuable findings."),
                        s("Report so it gets fixed",
                            "Each finding needs a title, severity with reasoning, clear reproduction steps, impact in business terms and a concrete fix. A report developers can act on in an hour beats a longer one nobody understands.")
                    ),
                    listOf(
                        qn("Which finding type do automated scanners miss most often?", 1,
                            "Missing security headers", "Business logic flaws", "Outdated library versions", "Open ports"),
                        qn("A good report includes", 1,
                            "Only screenshots", "Steps, impact and a concrete fix", "Exploit source only", "Client passwords")
                    ))
            )),

        // ───────────── ADVANCED ─────────────
        Course("c5", "Exploitation Methodology", Level.ADVANCED,
            "Turn assessment results into validated, minimal-harm proof of impact and understand escalation.",
            listOf(
                les("l9", "From Assessment to Exploitation", 16, 50,
                    listOf(
                        s("Prioritize with context",
                            "CVSS scores severity, but real risk also depends on exposure, exploitability, asset value and compensating controls. Check CISA's Known Exploited Vulnerabilities list and exploit-prediction data to focus on flaws attackers actually use."),
                        s("Validate safely",
                            "Exploitation in an engagement exists to prove impact, not to cause damage. Use lab replicas when possible, take backups where agreed, prefer proofs of concept that read a harmless marker, and stop as soon as impact is shown. Frameworks such as Metasploit organize modules for this work; use them only against authorized targets."),
                        s("Document the chain",
                            "Real compromises are chains: a low-severity leak plus a weak password plus a misconfigured service. Record each step, its evidence and timestamp so the client can break the chain at its weakest link.")
                    ),
                    listOf(
                        qn("Why check CISA's KEV list?", 0,
                            "It lists vulnerabilities known to be exploited in the wild", "It sells exploits", "It lists safe ports", "It replaces CVSS"),
                        qn("Goal of exploitation during an authorized test:", 1,
                            "Maximum damage", "Prove impact with minimal harm", "Steal real data", "Avoid reporting")
                    )),
                les("l10", "Privilege Escalation Concepts", 16, 55,
                    listOf(
                        s("Why escalation works",
                            "After initial access, attackers rarely have admin rights. They look for misconfiguration rather than magic: services running with excess privileges, weak file permissions, passwords stored in scripts or shell history, unpatched local flaws and overly broad sudo rules."),
                        s("Linux and Windows patterns",
                            "On Linux, review sudo rules, SUID binaries, cron jobs and writable service files. On Windows, review service permissions, unquoted service paths, stored credentials and token privileges. Auditing tools simply enumerate these settings so owners can fix them. Try the Privilege Audit lab to practice reading such exports."),
                        s("Detect and prevent",
                            "Apply least privilege, remove unnecessary SUID bits, require passwords for sudo, patch the kernel and services, and alert on new privileged accounts, unusual process parents and changes to scheduled tasks. auditd, Sysmon and EDR telemetry make escalation visible.")
                    ),
                    listOf(
                        qn("Most privilege escalation relies on", 0,
                            "Misconfiguration and weak permissions", "Faster CPUs", "Longer URLs", "DNS"),
                        qn("A good Windows detection telemetry source is", 0,
                            "Sysmon", "Notepad", "Paint", "Calculator")
                    ))
            )),

        Course("c6", "Active Directory and Enterprise", Level.ADVANCED,
            "How enterprise identity works, how attack paths form and how to harden and monitor them.",
            listOf(
                les("l11", "Active Directory Fundamentals", 16, 55,
                    listOf(
                        s("Core pieces",
                            "Active Directory stores users, groups and computers in a domain, organized in forests and OUs. Domain controllers authenticate with Kerberos and, for legacy cases, NTLM. Group Policy pushes settings and trusts link domains. Whoever controls a domain controller controls the domain."),
                        s("Why it is a prime target",
                            "AD is everywhere, so attackers study how permissions chain together. Graph tools such as BloodHound map who can reach what, revealing paths from an ordinary user to Domain Admin through nested groups, delegated rights and stale sessions. Defenders use the same graphs to cut those paths."),
                        s("Authentication weak points",
                            "Service accounts with weak passwords, unconstrained delegation, legacy protocols and cached admin credentials are recurring problems. Kerberos ticket abuse works because tickets are trusted, so protecting the krbtgt account and its keys is critical.")
                    ),
                    listOf(
                        qn("Who effectively controls the domain?", 0,
                            "Whoever controls a domain controller", "Any user on Wi-Fi", "The printer admin", "DNS clients"),
                        qn("BloodHound is used to", 0,
                            "Map permission and attack paths in AD", "Encrypt disks", "Block ads", "Send email")
                    )),
                les("l12", "Hardening and Detecting AD Attacks", 14, 55,
                    listOf(
                        s("Tiered administration",
                            "Use separate admin accounts for workstations, servers and domain controllers, and never log in to lower tiers with higher-tier credentials. Use Protected Users, privileged access workstations and just-in-time elevation."),
                        s("Credential hygiene",
                            "Deploy LAPS for unique local admin passwords, use gMSAs for services, enforce long passphrases, disable NTLM where possible and rotate krbtgt regularly. Remove unconstrained delegation and review ACLs for dangerous rights."),
                        s("Monitoring",
                            "Collect domain controller logs and alert on unusual Kerberos ticket requests, new members of privileged groups, directory replication requests from non-DCs and sudden spikes in LDAP queries. Honey accounts give high-confidence alerts.")
                    ),
                    listOf(
                        qn("LAPS provides", 0,
                            "Unique local admin passwords per machine", "Faster Wi-Fi", "Disk encryption", "Spam filtering"),
                        qn("Tiered administration means", 0,
                            "Separating admin accounts by privilege level", "Using one admin for everything", "Disabling logs", "Exposing domain controllers")
                    ))
            )),

        // ───────────── EXPERT ─────────────
        Course("c7", "Red Team and Purple Team", Level.EXPERT,
            "Threat-informed adversary emulation and turning it into better detection.",
            listOf(
                les("l13", "Planning a Red Team Engagement", 18, 70,
                    listOf(
                        s("Threat-informed objectives",
                            "Red teams emulate a specific adversary against agreed crown jewels, not everything. Pick ATT&CK techniques that match real groups targeting the sector, and define success as reaching an objective such as customer data, not as counting vulnerabilities."),
                        s("Operations and safety",
                            "Plan infrastructure, communications, deconfliction with the white cell, abort conditions and legal cover, including cloud and third-party provider rules. Operational security applies to the red team too: protect findings, logs and any captured data."),
                        s("Lessons, not trophies",
                            "The value is in the debrief. Map each action to what was detected, what was missed and why, then convert that into fixes: detection rules, hardening and response playbooks.")
                    ),
                    listOf(
                        qn("A red team engagement is measured mainly by", 0,
                            "Objectives reached and detection gaps found", "Number of scans", "Tool count", "Typing speed"),
                        qn("The white cell", 0,
                            "Coordinates, deconflicts and controls the exercise", "Plays the attacker", "Writes malware", "Sells reports")
                    )),
                les("l14", "Purple Teaming and Detection Engineering", 16, 70,
                    listOf(
                        s("Attack and defend together",
                            "Purple teaming runs attacker techniques while defenders watch and tune live. Each technique is executed, checked against telemetry and marked detected, logged but not alerted, or missed. Projects like Atomic Red Team provide small, safe, reproducible tests mapped to ATT&CK."),
                        s("Writing detections",
                            "Good detections target behavior, not exact file names. Sigma gives a vendor-neutral rule format that translates to your SIEM. Test for false positives against normal admin activity and document the response steps."),
                        s("Measure coverage",
                            "Track ATT&CK coverage, mean time to detect and respond, and the ratio of true to false alerts. Coverage heatmaps show where to invest next.")
                    ),
                    listOf(
                        qn("Sigma is", 0,
                            "A vendor-neutral detection rule format", "A malware family", "A hashing algorithm", "A VPN"),
                        qn("Good detections focus on", 0,
                            "Behaviors", "Single file names", "Attacker IPs only", "Time of day")
                    ))
            )),

        Course("c8", "Malware Analysis and Incident Response", Level.EXPERT,
            "Analyze hostile code safely, respond to incidents and hunt what alerts missed.",
            listOf(
                les("l15", "Safe Malware Analysis", 18, 75,
                    listOf(
                        s("Build a safe lab",
                            "Analyze only inside isolated virtual machines with snapshots, no shared folders and a host-only or simulated network. Never run suspicious files on a real or work machine. Assume anything you open may try to escape or phone home."),
                        s("Static then dynamic",
                            "Static analysis inspects a file without running it: hashes, strings, imports, headers and packers, with tools like Ghidra for disassembly. Dynamic analysis observes behavior in a sandbox: files, registry, processes and network. Together they reveal capabilities and indicators."),
                        s("Turn findings into defense",
                            "Extract indicators of compromise, but prefer behavior-based detection that survives recompilation. Write YARA rules for families and share intelligence through trusted channels.")
                    ),
                    listOf(
                        qn("Where should suspicious files be analyzed?", 0,
                            "An isolated VM with snapshots", "Your daily laptop", "A shared server", "A phone"),
                        qn("Static analysis means", 0,
                            "Examining a file without executing it", "Running it as admin", "Deleting it", "Encrypting it")
                    )),
                les("l16", "Incident Response and Threat Hunting", 18, 75,
                    listOf(
                        s("The IR lifecycle",
                            "NIST describes preparation, detection and analysis, containment, eradication, recovery and lessons learned. Speed matters, but so does evidence: preserve memory and logs before wiping, track chain of custody and communicate through pre-agreed channels."),
                        s("Triage and forensics",
                            "Scope the incident by finding patient zero, affected accounts and data. Use disk, memory and log forensics with tools like Volatility and Autopsy, build a timeline and choose containment steps that limit harm without destroying evidence."),
                        s("Hunting proactively",
                            "Threat hunters form hypotheses based on ATT&CK, search telemetry for behaviors that alerts missed and turn successful hunts into permanent detections. Hunting assumes the attacker may already be inside.")
                    ),
                    listOf(
                        qn("First priority after confirming an incident?", 0,
                            "Contain while preserving evidence", "Wipe everything immediately", "Ignore until Monday", "Post on social media"),
                        qn("Threat hunting assumes", 0,
                            "The attacker may already be inside", "The network is perfect", "Alerts catch everything", "No logs exist")
                    ))
            ))
    )

    // ───────────── LABS (simulated sandboxes) ─────────────
    val labs: List<Lab> = listOf(
        Lab("lab1", "Log Detective", Level.BEGINNER, 50,
            "A web server was breached. Read the logs, find the attacker and submit the flag.",
            mapOf(
                "README.txt" to "MISSION: A server was compromised. Review the logs and identify the attacker IP.\nFLAG FORMAT: flag{A_B_C_D} where A.B.C.D is the attacker IP written with underscores.\nTry: ls, ls logs, cat logs/auth.log, grep Accepted logs/auth.log",
                "logs/auth.log" to "Nov 05 02:11:01 srv sshd[411]: Failed password for root from 198.51.100.23 port 51122\n" +
                    "Nov 05 02:11:09 srv sshd[412]: Failed password for admin from 203.0.113.77 port 40211\n" +
                    "Nov 05 02:11:10 srv sshd[413]: Failed password for admin from 203.0.113.77 port 40212\n" +
                    "Nov 05 02:11:12 srv sshd[414]: Failed password for admin from 203.0.113.77 port 40213\n" +
                    "Nov 05 02:11:15 srv sshd[415]: Failed password for admin from 203.0.113.77 port 40214\n" +
                    "Nov 05 02:13:44 srv sshd[455]: Accepted password for admin from 203.0.113.77 port 40290\n" +
                    "Nov 05 03:01:02 srv sshd[502]: Accepted publickey for deploy from 192.0.2.10 port 50500",
                "logs/web.log" to "192.0.2.10 - - GET /index.html 200\n203.0.113.77 - - GET /admin 200\n203.0.113.77 - - GET /backup.zip 200\n198.51.100.23 - - GET /login 200",
                "notes/analyst.txt" to "Reminder: many failures followed by a success from the same IP is a classic brute-force signature."
            ),
            listOf("Start with ls and read README.txt.", "grep Accepted logs/auth.log shows who got in.", "Compare the IP that succeeded with the one that failed many times first."),
            "flag{203_0_113_77}"),

        Lab("lab2", "Recon Walkthrough", Level.INTERMEDIATE, 60,
            "Work through simulated scan output and website files to find a forgotten backup.",
            mapOf(
                "README.txt" to "TARGET: shop.example.test (simulated, authorized).\nA forgotten backup holds the flag. Explore with ls and cat. Recon starts with what the server already tells you.",
                "scan/nmap.txt" to "PORT      STATE SERVICE VERSION\n22/tcp    open  ssh     OpenSSH 7.4\n80/tcp    open  http    nginx 1.14\n3306/tcp  open  mysql   MySQL 5.7 (exposed!)",
                "web/index.html" to "<h1>Shop</h1>",
                "web/robots.txt" to "User-agent: *\nDisallow: /admin\nDisallow: /old-backup",
                "web/old-backup/config.bak" to "db_host=localhost\ndb_user=shop\n# deploy note: remove this file!\nFLAG=flag{robots_txt_is_not_security}"
            ),
            listOf("Check what robots.txt asks crawlers to avoid.", "ls web/old-backup then cat the file inside."),
            "flag{robots_txt_is_not_security}"),

        Lab("lab3", "Secure Code Review", Level.INTERMEDIATE, 80,
            "Read the source files and name the vulnerable function and its class.",
            mapOf(
                "README.txt" to "Review the files in src/. Exactly one function is vulnerable.\nFLAG FORMAT: flag{vulnerability_function} for example flag{xss_render_page}.",
                "src/db.py" to "def get_user(name):\n    query = \"SELECT * FROM users WHERE name = '\" + name + \"'\"\n    return db.execute(query)\n\ndef get_order(order_id):\n    return db.execute(\"SELECT * FROM orders WHERE id = ?\", (order_id,))",
                "src/views.py" to "from html import escape\n\ndef greet(name):\n    return \"<p>Hello \" + escape(name) + \"</p>\"",
                "notes/reviewer.txt" to "Question: which function builds SQL from user input by string concatenation?"
            ),
            listOf("cat src/db.py and compare the two functions.", "The class is sql_injection."),
            "flag{sql_injection_get_user}"),

        Lab("lab4", "Crypto Vault", Level.ADVANCED, 120,
            "A message was intercepted. It is encoded, not encrypted. Peel the layers.",
            mapOf(
                "README.txt" to "An intercepted message is in vault/message.txt.\nTools: decode b64 <text>, decode rot13 <text>, decode hex <text>.",
                "vault/message.txt" to MSG4,
                "vault/hint.txt" to "Layer 1 looks like base64. Whatever it reveals still looks scrambled."
            ),
            listOf("Decode the message with decode b64.", "The result is still scrambled: it is a classic letter-rotation cipher.", "Use decode rot13 on the result."),
            FLAG4),

        Lab("lab5", "Privilege Audit", Level.ADVANCED, 150,
            "Audit exported configuration files and count every misconfiguration marked [RISK].",
            mapOf(
                "README.txt" to "Audit the exports in audit/. Lines marked [RISK] are misconfigurations.\nFLAG FORMAT: flag{risks_N} where N is the total number of [RISK] lines across all files.\nTry: grep RISK audit/sudoers.txt",
                "audit/sudoers.txt" to "# sudoers export\nroot ALL=(ALL) ALL\n[RISK] dev ALL=(ALL) NOPASSWD: ALL\nbackup ALL=(root) /usr/bin/rsync",
                "audit/suid.txt" to "/usr/bin/passwd\n/usr/bin/sudo\n[RISK] /usr/bin/find (SUID set)\n[RISK] /opt/tools/runner (SUID, world-writable)",
                "audit/cron.txt" to "0 * * * * root /opt/scripts/clean.sh\n[RISK] /opt/scripts/clean.sh is writable by group users",
                "audit/services.txt" to "sshd: ok\nnginx: runs as www-data\n[RISK] legacy-agent: runs as root with password in config"
            ),
            listOf("There are four files in audit/. Check each with grep RISK <file>.", "Add the counts together."),
            "flag{risks_5}")
    )

    // ───────────── TOOLS ─────────────
    val tools: List<Tool> = listOf(
        Tool("Nmap", "Recon", "Network discovery and port/service scanner.",
            "Find live hosts, open ports and service versions inside an authorized scope.",
            "Watch for scan patterns in IDS and firewall logs; expose only needed services."),
        Tool("Wireshark", "Network", "Packet capture and protocol analyzer.",
            "Inspect traffic, follow streams and spot cleartext credentials or beaconing.",
            "Use TLS everywhere, segment networks and monitor east-west traffic."),
        Tool("Burp Suite", "Web", "Intercepting proxy for web application testing.",
            "Map the app, replay and modify requests, test authentication and input handling.",
            "Validate on the server, tune the WAF and harden session handling."),
        Tool("OWASP ZAP", "Web", "Open-source web scanner and proxy.",
            "Run baseline scans in CI pipelines and explore manually.",
            "Fix findings in the pipeline before release."),
        Tool("ffuf / Gobuster", "Web", "Content discovery for hidden paths and files.",
            "Find forgotten admin pages and backups on authorized sites.",
            "Remove unused files, restrict admin paths and rate-limit requests."),
        Tool("Metasploit Framework", "Exploit", "Framework that organizes exploit and post-exploitation modules.",
            "Validate whether known flaws are really exploitable in labs or scoped tests.",
            "Patch quickly, deploy EDR and alert on exploit behavior."),
        Tool("John / Hashcat", "Passwords", "Offline password hash auditing tools.",
            "Audit your own hashes for weak or reused passwords.",
            "Use long passphrases, MFA and slow hashes like Argon2 or bcrypt."),
        Tool("Ghidra", "Analysis", "Open-source reverse engineering suite.",
            "Disassemble and decompile binaries to understand behavior.",
            "Turn findings into detections and YARA rules."),
        Tool("Volatility", "Analysis", "Memory forensics framework.",
            "Examine memory dumps for processes, connections and injected code.",
            "Capture memory early in incident response."),
        Tool("YARA", "Analysis", "Pattern-matching rules to classify malware.",
            "Hunt files and memory for known families and traits.",
            "Combine with behavior-based detections."),
        Tool("BloodHound", "AD", "Graph tool that maps Active Directory permission paths.",
            "Reveal paths from low-privilege users to Domain Admin.",
            "Cut paths with tiering, ACL cleanup and LAPS."),
        Tool("CyberChef", "Utility", "Browser app for encoding, decoding and data transforms.",
            "Peel layers such as base64, hex and rot13 when analyzing samples.",
            "Keep sensitive data out of online tools; use an offline copy.")
    )

    val checklist: List<CheckItem> = listOf(
        CheckItem("mfa", "Turn on multi-factor authentication"),
        CheckItem("pm", "Use a password manager"),
        CheckItem("upd", "Enable automatic updates"),
        CheckItem("bkp", "Keep an offline or versioned backup"),
        CheckItem("wifi", "Use WPA3/WPA2 with a strong Wi-Fi password"),
        CheckItem("vpn", "Use a trusted VPN on public Wi-Fi"),
        CheckItem("phish", "Verify links before tapping (phishing)"),
        CheckItem("lock", "Use a screen lock and device encryption"),
        CheckItem("apps", "Install apps only from trusted stores"),
        CheckItem("reuse", "Never reuse passwords across sites")
    )
}
