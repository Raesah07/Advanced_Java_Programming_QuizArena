# QuizArena

QuizArena is a Java 17 Swing/TCP multiplayer quiz application backed by MySQL. It demonstrates layered OOP design, JDBC prepared statements and transactions, password hashing, socket clients, thread-safe rooms, scheduled round timers, and live leaderboards.

## Run on Windows

1. Install MySQL 8+. A compatible JDK 24 was detected on the development machine; Java 17+ is supported. Maven 3.9.16 is included locally in `.tools`.
2. Run `mysql -u root -p < database/schema.sql` and then `mysql -u root -p quizarena < database/seed.sql`.
3. Set `QUIZ_DB_URL`, `QUIZ_DB_USER`, and `QUIZ_DB_PASSWORD` (defaults target local MySQL database `quizarena`).
4. In PowerShell, from this folder, start the server with `.un-server.ps1`.
5. Open another PowerShell window in this folder and start one or more clients with `.un-client.ps1`.

If PowerShell blocks local scripts, run `Set-ExecutionPolicy -Scope Process Bypass` first. The scripts use a temporary `X:` drive because Java/Maven can misread the Chinese characters in the OneDrive path.

Connection settings are stored in `.env`. Edit `QUIZ_DB_PASSWORD` there if your MySQL root account has a password. `.env` is excluded from version control.

Useful Maven commands. Use `mvn-local.ps1` because the project path contains Chinese characters that can confuse Java's classpath handling:

```powershell
# Compile and package
.\mvn-local.ps1 package

# Start server directly
.\mvn-local.ps1 exec:java '-Dexec.mainClass=com.quizarena.server.QuizServer'

# Start client directly
.\mvn-local.ps1 exec:java '-Dexec.mainClass=com.quizarena.client.QuizClient'
```

Manual equivalent:

```powershell
subst X: (Get-Location).Path
Set-Location X:\
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-24'
$env:MAVEN_HOME = 'X:\.tools\apache-maven-3.9.16'
& 'X:\.tools\apache-maven-3.9.16\bin\mvn.cmd' exec:java '-Dexec.mainClass=com.quizarena.server.QuizServer'
```

The seeded admin account is `admin@example.com` / `admin123` (change it before deployment). The seed file currently installs 30 Java Programming questions across three difficulty levels. Create a room from the dashboard, share its code, and join it from other clients. The server owns question selection, answer validation, scoring, and room state; clients never send scores.

Completed quiz results are persisted in `quiz_sessions` and `session_players`. The **LIVE LEADERBOARD** button loads the durable global leaderboard from the `global_leaderboard` SQL view, so completed scores remain available after the server restarts. Running `database/seed.sql` intentionally replaces the question bank and clears prior quiz history/results.

## Structure

`model` contains serializable protocol/domain records, `dao` contains JDBC access, `service` contains authentication, `network` contains the line-delimited JSON-free protocol, `server` owns concurrent rooms, and `ui` contains Swing screens.
