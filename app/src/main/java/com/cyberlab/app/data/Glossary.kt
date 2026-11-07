package com.cyberlab.app.data

data class Term(val term: String, val def: String)

object Glossary {
    val terms: List<Term> = listOf(
        Term("Attack surface", "Every point where an attacker could try to enter or extract data."),
        Term("Authentication", "Proving who you are, for example with a password, key or biometric."),
        Term("Authorization", "Deciding what an authenticated identity is allowed to do."),
        Term("Backdoor", "A hidden way to access a system that bypasses normal controls."),
        Term("Botnet", "A network of compromised devices controlled remotely, often used for DDoS."),
        Term("Brute force", "Trying many possible secrets until one works. Rate limits and lockouts slow it down."),
        Term("Buffer overflow", "A memory bug where input overruns a buffer and corrupts nearby data."),
        Term("CVE", "A public identifier for a specific known vulnerability, such as CVE-2021-44228."),
        Term("CVSS", "A 0 to 10 score describing the technical severity of a vulnerability."),
        Term("Defense in depth", "Layering several controls so one failure does not mean total compromise."),
        Term("DDoS", "Overwhelming a service with traffic from many sources to make it unavailable."),
        Term("DMZ", "A network zone for public-facing servers, separated from the internal network."),
        Term("Encryption", "Scrambling data so only holders of the right key can read it."),
        Term("Firewall", "A control that allows or blocks network traffic based on rules."),
        Term("Hash", "A one-way fingerprint of data. Used for integrity checks and password storage."),
        Term("Honeypot", "A decoy system built to attract attackers and reveal their behavior."),
        Term("IDS / IPS", "Systems that detect (IDS) or detect and block (IPS) suspicious activity."),
        Term("Malware", "Software designed to harm, spy on or take control of a system."),
        Term("MFA", "Multi-factor authentication: combining something you know, have or are."),
        Term("Man-in-the-middle", "An attacker secretly relaying or altering traffic between two parties."),
        Term("Payload", "The part of an attack that performs the final action once access is gained."),
        Term("Penetration test", "An authorized simulated attack used to find weaknesses before criminals do."),
        Term("Phishing", "Tricking people into revealing secrets or running something via fake messages."),
        Term("Ransomware", "Malware that encrypts data and demands payment for access."),
        Term("Red team", "A group that emulates real adversaries to test an organization's detection and response."),
        Term("Rootkit", "Malware that hides itself and other activity deep in the operating system."),
        Term("SIEM", "A platform that collects and correlates logs to detect security events."),
        Term("Social engineering", "Manipulating people rather than machines to gain access."),
        Term("SQL injection", "Untrusted input changes a database query. Fixed with parameterized queries."),
        Term("Threat model", "A structured view of what you protect, from whom and how it could fail."),
        Term("Vulnerability", "A weakness that could be used to break a security property."),
        Term("XSS", "Cross-site scripting: untrusted input runs as script in another user's browser."),
        Term("Zero-day", "A flaw unknown to the vendor, so no patch exists yet."),
        Term("Zero trust", "Never trust by network location; verify every user, device and request.")
    ).sortedBy { it.term }

    val cheats: List<Pair<String, List<Pair<String, String>>>> = listOf(
        "Common ports" to listOf(
            "21" to "FTP", "22" to "SSH", "25" to "SMTP", "53" to "DNS", "80" to "HTTP",
            "88" to "Kerberos", "110" to "POP3", "143" to "IMAP", "389" to "LDAP",
            "443" to "HTTPS", "445" to "SMB", "3306" to "MySQL", "3389" to "RDP", "5432" to "PostgreSQL"
        ),
        "HTTP status codes" to listOf(
            "200" to "OK", "301" to "Moved permanently", "302" to "Found (temporary redirect)",
            "400" to "Bad request", "401" to "Unauthenticated", "403" to "Forbidden",
            "404" to "Not found", "429" to "Too many requests", "500" to "Server error", "503" to "Unavailable"
        ),
        "Linux triage" to listOf(
            "ls -la" to "List files with permissions",
            "grep -ri word ." to "Search text recursively",
            "ss -tulpn" to "Listening ports and owners",
            "last -a" to "Recent logins",
            "journalctl -u ssh" to "Logs for one service",
            "chmod 640 f" to "Owner rw, group r, others none"
        ),
        "Incident response" to listOf(
            "1" to "Prepare", "2" to "Detect and analyze", "3" to "Contain",
            "4" to "Eradicate", "5" to "Recover", "6" to "Lessons learned"
        )
    )
}
