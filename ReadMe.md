### A Vintage Library System Built With Java and JavaFX

This is a library system I built way back when I was learning programming. Feel free to clone, fork etc. and correct
my mistakes. I know that the codebase is one long noodle :-)

To run the code, any Java SDK above 11 should work. Run this on the command line (be sure to replace all '/path/to' to your actual file locations. For the JavaFX SDK, download it here: [Download Link](https://gluonhq.com/products/javafx/).
Be sure to select your operating system and hardware architecture. Also, I've included libraries that support the M1 architecture (aarch64/arm64) so be sure to select this also when downloading JavaFX (if it's an M1 chip and above). Also, aarch64 and arm64 mean the same thing, so select whichever option's available.

```bash
/path/to/java/runtime/bin/java --module-path /path/to/JavaFX/SDK/lib --add-modules=javafx.controls,javafx.fxml -Djava.library.path=/path/to/JavaFX/SDK/lib -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /path/to/compiled/classes System:/path/to/where/you/cloned/this/repo/lib/json-simple-1.1.1.jar:/path/to/where/you/cloned/this/repo/lib/javax.json-api-1.0.jar:/path/to/where/you/cloned/this/repo//lib/HikariCP-java7-2.4.13.jar:/path/to/where/you/cloned/this/repo//lib/slf4j-simple-1.5.5.jar:/path/to/where/you/cloned/this/repo//lib/slf4j-api-1.5.5.jar:/path/to/where/you/cloned/this/repo/lib/javax.json-1.1.jar:/path/to/where/you/cloned/this/repo//lib/sqlite-jdbc-3.34.0.jar:/path/to/JavaFX/SDK/lib/javafx-swt.jar:/path/to/JavaFX/SDK/lib/javafx.web.jar:/path/to/JavaFX/SDK/lib/javafx.base.jar:/path/to/JavaFX/SDK/lib/javafx.fxml.jar:/path/to/JavaFX/SDK/lib/javafx.media.jar:/path/to/JavaFX/SDK/lib/javafx.swing.jar:/path/to/JavaFX/SDK/lib/javafx.controls.jar:/path/to/JavaFX/SDK/lib/javafx.graphics.jar librarysystem.gui.Login
```