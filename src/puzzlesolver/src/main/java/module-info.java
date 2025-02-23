module com.puzzle {
    requires  transitive  javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    
    opens com.puzzle to javafx.fxml,java.desktop;
    exports com.puzzle;
    exports com.puzzle.core;
    exports com.puzzle.utils;
}
