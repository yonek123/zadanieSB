# ZadanieSB

## Treść zadania - wykonania logreadera (aplikacja standalone)

Wymagania dotyczące działania aplikacji:
1) po starcie szuka na dysku D: katalogu o nazwie 'logs',
2) po znalezieniu katalogu, aplikacja plik po pliku interpretuje pliki z logami w kolejności lastModified descending,
3) na koniec działania lub po każdym pliku aplikacja wylistowuje w konsoli:
- czas jaki upłynął na czytanie pliku,
- zakres logów w pliku (czyli różnica czasu miedzy pierwszym logiem w pliku a ostatnim),
- ilość logów pogrupowana wg severity (np. INFO, WARN, ERROR),
- stosunek ilości logów o severity ERROR lub wyższym do wszystkich logów,
- ilość unikalnych wystąpień bibliotek w logu (ta wartość w kwadratowych nawiasach zaraz po oznaczeniu severity, np: [org.jboss.as.server]).

Dodatkowo zaproponuj strukturę tabeli bazodanowej, która mogłaby posłużyć do przechowywania logów znajdujących się w pliku w bazie.

## Działanie aplikacji

Aby uruchomić aplikację należy:
1. Uruchomić cmd;
2. Przejść do katalogu executable (polecenie "cd [ścieżka do katalogu z projektem]\executable");
3. Wykonać komendę "java -jar zadanieSB.jar".

Alternatywnie aplikację uruchomić można z poziomu kodu.

Program po uruchomieniu wyświetli wyniki działania w konsoli.
W przypadku, gdy katalog "D:\logs" nie istnieje lub jest pusty wyświetlone zostaną sotsowne informacje.

## Struktura tabeli
W katalogu "struktura tabeli" znajduje się graficzna reprezentacja struktury tabeli przechowującej logi oraz implementacja w formie pliku sql zawierającego zapytanie tworzące tabelę. Zapytanie jest zgodne z systemem MySQL 8.0.

Przykładowe zapytanie:
INSERT INTO Logs (LogDateTime, LogSeverity, LogLibrary, LogThread, LogDetails) VALUES
("2018-12-04 11:37:20.321", "INFO",  "org.jboss.modules", "(main)", "JBoss Modules version 1.4.3.Final"),
("2017-12-04 11:37:20.833", "DEBUG",  "org.jboss.as.config", "(MSC service thread 1-7)", "Configured system properties"),
("2017-12-04 11:37:22.280", "FATAL",  "org.jboss.as.server", "(Controller Boot Thread)", "WFLYSRV0056: Server boot has failed in an unrecoverable manner; exiting. See previous messages for details."),
("2017-12-12 11:36:52.047", "ERROR",  "org.jboss.msc.service.fail", "(ServerService Thread Pool -- 59)", "MSC000001:Failed to start service jboss.persistenceunit: org.jboss.msc.service.StartException in service jboss.persistenceunit: org.hibernate.HibernateException: Access to DialectResolutionInfo cannot be null when 'hibernate.dialect' not set
	at org.jboss.as.jpa.service.PersistenceUnitServiceImpl$1$1.run(PersistenceUnitServiceImpl.java:172)
	at org.jboss.as.jpa.service.PersistenceUnitServiceImpl$1$1.run(PersistenceUnitServiceImpl.java:117)
	at org.wildfly.security.manager.WildFlySecurityManager.doChecked(WildFlySecurityManager.java:665)
	at org.jboss.as.jpa.service.PersistenceUnitServiceImpl$1.run(PersistenceUnitServiceImpl.java:182)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
	at java.lang.Thread.run(Thread.java:745)
	at org.jboss.threads.JBossThread.run(JBossThread.java:320)
Caused by: org.hibernate.HibernateException: Access to DialectResolutionInfo cannot be null when 'hibernate.dialect' not set
	at org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl.determineDialect(DialectFactoryImpl.java:104)
	at org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl.buildDialect(DialectFactoryImpl.java:71)
	at org.hibernate.engine.jdbc.internal.JdbcServicesImpl.configure(JdbcServicesImpl.java:205)")

Warto zwrócić uwagę na to, że milisekundy są poprzedzone kropką, a nie przecinkiem. Ma to związek z formatem danych typu DATETIME. Jeśli dane z logów miały by być automatycznie dodawane do bazy danych to należało by umieścić w kodzie funkcję zamieniającą przecinek na kropkę dla tej kolumny, albo zastąpić typ kolumny DATETIME w bazie danych typem tekstowym.
