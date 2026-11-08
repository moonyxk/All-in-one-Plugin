# All in One Plugin

Ein Plugin für **Paper 26.3**. Entwickelt für **Java 25**.

## Funktionen

| Befehl | Funktion |
| --- | --- |
| `/heal <Spieler>` | Heilt den Spieler und füllt seine Hungeranzeige auf. |
| `/feed <Spieler>` | Füllt die Hungeranzeige des Spielers auf. |
| `/fly <Spieler>` | Schaltet das Fliegen für den Spieler ein oder aus. |
| `/boom <Spieler> [true/false]` | Erzeugt beim Spieler eine Explosion, die keine Blöcke zerstört. Der optionale Wert schaltet den Schaden an Entities ein oder aus (Standard: `true`). |
| `/kick <Spieler> <Grund>` | Entfernt einen Spieler mit Begründung vom Server. |
| `/menu` | Öffnet dein persönliches Menü für ChestPunch und den ChestPunch-Sound. |

Die Moderator-Befehle benötigen die Berechtigung `allinoneplugin.mod`, die standardmäßig Operatoren vorbehalten ist. `/menu` ist für alle Spieler verfügbar. Bei den Moderator-Befehlen ist der Spielername verpflichtend; sie können auch über die Serverkonsole ausgeführt werden.

## Umschaltbare Funktionen (`/sigma`)

`/sigma` öffnet für Spieler mit der Berechtigung `allinoneplugin.sigma` (standardmäßig Operatoren) das Servermenü. Spieler ohne diese Berechtigung gelangen in ihr persönliches Menü wie mit `/menu`; serverweite Einstellungen können sie nicht über `/sigma <Funktion> [on|off]` ändern. Im Servermenü lassen sich Funktionen per Linksklick aktivieren oder deaktivieren. Bei `NoCreeperGrief` schaltet ein Rechtsklick zusätzlich den Creeper-Schaden um. Berechtigte Spieler können die Funktionen auch direkt per Befehl umschalten; über die Konsole geht das ebenfalls. Ohne `on`/`off` wird umgeschaltet. Die Zustände werden unter `features` in der `config.yml` gespeichert.

| Funktion | ID | Beschreibung |
| --- | --- | --- |
| ChestPunch | `chest-punch` | Ein Linksklick mit einem Gegenstand auf eine Truhe, Falltruhe oder ein Fass (nicht mit einer Axt) bzw. auf einen Spender, Werfer oder Trichter (nicht mit einer Spitzhacke) legt den Stapel hinein. Nicht beim Schleichen. Jeder Spieler kann ChestPunch und den Sound im persönlichen `/menu` unabhängig für sich ein- oder ausschalten. Alternativ schaltet `/chestpunch` ChestPunch für den Spieler um (Berechtigung `allinoneplugin.chestpunch`, standardmäßig für alle). |
| NoCreeperGrief | `no-creeper-grief` | Creeper-Explosionen zerstören keine Blöcke; ob sie Spielern und Entities Schaden zufügen, lässt sich per Rechtsklick im Menü umschalten. |
| NoEndermanGrief | `no-enderman-grief` | Verhindert, dass Endermen Blöcke aufnehmen. Das Platzieren bereits getragener Blöcke bleibt unverändert. |

Zusätzliche Funktionen:

- Anpassbare Begrüßungsnachricht beim Betreten des Servers; `%player%` wird durch den Spielernamen ersetzt.
- Eine Nachricht im Chat und in der Konsole, wenn ein Diamanterzblock abgebaut wird.
- Beim Schreiben im Chat wird die eigene Nachricht erneut angezeigt und zusammen mit dem Spielernamen in der Konsole ausgegeben.
- Beim Betreten wird zusätzlich eine Meldung an alle Spieler gesendet.
- Bei jedem Serverstart prüft das Plugin asynchron das neueste stabile GitHub-Release.
  Ist es neuer als die installierte Version, meldet die Konsole die konfigurierten Update-Hinweise.
  Der Serverstart wird nicht blockiert; Netzwerkfehler werden als Warnung protokolliert.
  Updates werden nicht automatisch heruntergeladen oder installiert.

## Konfiguration

In der `config.yml` lassen sich die persönliche Begrüßung (`join-message`), die Beitrittsmeldung
für alle Spieler (`join-broadcast-message`) und die Statusmeldungen unter `messages` anpassen.
Meldungen unterstützen `&`-Farbcodes; `%player%` steht für den Spielernamen und `%feature%`
für den Namen der umgeschalteten Funktion.

Die Update-Prüfung lässt sich mit `update-check.enabled: false` deaktivieren. Die Hinweise bei
verfügbaren Updates sind ebenfalls konfigurierbar; `%latest%`, `%current%` und `%url%` werden
durch die verfügbare, installierte Version und den Release-Link ersetzt. Operatoren erhalten
den konfigurierbaren Hinweis außerdem bei jedem Betreten des Servers.

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
target/All-in-One-Plugin-1.0.jar
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
