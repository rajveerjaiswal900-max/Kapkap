# Security Policy

## Reporting a vulnerability

Please do not post API keys, access tokens, signing keys, or personal data in public issues.

This repository currently has no dedicated private vulnerability-reporting channel configured. If you discover a security issue, avoid sharing exploit details publicly; contact the repository owner through an appropriate private channel before disclosure.

## App security notes

- Use HTTPS endpoints.
- Never hard-code production provider keys in the source.
- Prefer a server-side proxy that stores secrets securely.
- Do not commit keystores, passwords, tokens, or local configuration.
