# All in One Plugin

Ein Plugin für **Paper 26.3**. Entwickelt für **Java 25**.

## Funktionen

| Befehl | Funktion |
| --- | --- |
| `/test` | Gibt eine Testnachricht aus. |
| `/heal` | Heilt dich und füllt deine Hungeranzeige auf. |
| `/feed` | Füllt deine Hungeranzeige auf. |
| `/fly` | Schaltet das Fliegen ein oder aus. |
| `/boom` | Erzeugt eine Explosion, die keine Blöcke zerstört. |
| `/kick <Spieler> <Grund>` | Entfernt einen Spieler mit Begründung vom Server. Standardmäßig nur für Operatoren (`allinoneplugin.kick`). |
| `/balance` | Zeigt dein Coins-Guthaben an. |
| `/pay <Spieler> <Betrag>` | Sendet Coins an einen anderen Spieler. |
| `/shop <Item> <Preis>` | Kauft ein Item für den angegebenen Preis. |

Zusätzliche Funktionen:

- Anpassbare Begrüßungsnachricht beim Betreten des Servers; `%player%` wird durch den Spielernamen ersetzt.
- Eine Nachricht im Chat und in der Konsole, wenn ein Diamanterzblock abgebaut wird.
- Beim Schreiben im Chat wird die eigene Nachricht erneut angezeigt und zusammen mit dem Spielernamen in der Konsole ausgegeben.
- Beim Betreten wird zusätzlich eine Meldung an alle Spieler gesendet.

## Voraussetzungen

- Minecraft-Server **Paper 26.3**
- **Java 25** für Paper und den Build
- **Maven** zum Bauen des Plugins

Das Plugin verwendet die Paper-API. Es benötigt zur Laufzeit keine separat mitgelieferten Paper- oder Bukkit-Bibliotheken.

## Bauen

Im Projektverzeichnis ausführen:

```shell
mvn clean package
```

Das Plugin-JAR wird hier erzeugt:

```text
target/All-in-One-Plugin-1.0-SNAPSHOT.jar
```

Kopiere die JAR-Datei in den `plugins`-Ordner deines Paper-Servers und starte den Server.

## Build-System

Dieses Projekt verwendet **Maven**, nicht Gradle. Ein Gradle-Wrapper (`gradlew`) ist nicht vorhanden. Zum Bauen verwendest du `mvn clean package`.

## GitHub Actions

- Bei Pushes und Pull Requests baut und testet GitHub Actions das Plugin. Das JAR steht anschließend als Workflow-Artefakt zum Download bereit.
- Für jedes veröffentlichte Release, auch manuell erstellte Releases und Vorabversionen, wird das Plugin gebaut, getestet und als JAR angehängt. Entwürfe werden spätestens bei ihrer Veröffentlichung berücksichtigt.
- Ein Versions-Tag wie `v1.3.0` erstellt ein GitHub-Release mit dem passenden JAR und generierten Release-Notizen. Tags mit einem Zusatz wie `v1.3.0-rc.1` werden als Vorabversion veröffentlicht.

Für lokale Builds steht die Plugin-Version als `plugin.version` in der `pom.xml`. Bei Releases wird sie automatisch aus dem Tag übernommen; Zeichen außerhalb von Buchstaben, Ziffern, Punkt, Unterstrich und Bindestrich werden durch Bindestriche ersetzt.

Ein vorhandenes Release-JAR mit demselben Namen wird ersetzt. GitHub startet keinen Release-Workflow für Releases, die ein anderer Workflow mit `GITHUB_TOKEN` erstellt; dieser muss das JAR selbst anhängen. Bei unveränderlichen Releases müssen Assets bereits vor der Veröffentlichung angehängt werden.

## Einschränkungen

- Coins werden nur im Arbeitsspeicher gespeichert und gehen beim Neustart des Servers verloren.
- `/shop` verwendet den beim Befehl eingegebenen Preis. Auch ein negativer Preis wird derzeit akzeptiert; der Economy-Code zahlt in diesem Fall Coins aus, statt sie abzuziehen. Verwende den Shop deshalb nur mit vertrauenswürdigen Spielern.
- Nur `/kick` ist durch ein Plugin-Permission-Node beschränkt; dieses Node ist standardmäßig Operatoren vorbehalten. Für die anderen Befehle sind keine eigenen Berechtigungen definiert.
