package io.github.morichan.retuss.window;

import io.github.morichan.retuss.window.diagram.ContentType;
import io.github.morichan.retuss.window.diagram.NodeDiagram;
import io.github.morichan.retuss.window.diagram.RelationshipAttributeGraphic;
import io.github.morichan.retuss.window.utility.UtilityJavaFXComponent;
import io.github.morichan.retuss.language.uml.Package;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <p> RETUSSメインウィンドウの動作管理クラス </p>
 *
 * <p>
 * {@link RetussWindow}クラスで用いているretussMain.fxmlファイルにおけるシグナルハンドラを扱います。
 * </p>
 */
public class MainController {
    @FXML
    private Button normalButtonInCD;
    @FXML
    private Button classButtonInCD;
    @FXML
    private Button noteButtonInCD;
    @FXML
    private Button compositionButtonInCD;
    @FXML
    private Button generalizationButtonInCD;
    @FXML
    private ScrollPane classDiagramScrollPane;
    @FXML
    private Canvas classDiagramCanvas;

    private List<Button> buttonsInCD = new ArrayList<>();

    private TextInputDialog classNameInputDialog;

    private Stage codeStage;
    private CodeController codeController;

    private UtilityJavaFXComponent util = new UtilityJavaFXComponent();
    private ClassDiagramDrawer classDiagramDrawer = new ClassDiagramDrawer();

    /**
     * <p> JavaFXにおけるデフォルトコンストラクタ </p>
     *
     * <p>
     * Javaにおける通常のコンストラクタ（ {@code Controller()} メソッド）は使えないため、
     * {@link RetussWindow} クラス内でのFXMLファイル読込み時に {@link #initialize()} メソッドを呼び出す仕様になっています。
     * </p>
     */
    @FXML
    private void initialize() {
        buttonsInCD.addAll(Arrays.asList(normalButtonInCD, classButtonInCD, noteButtonInCD, compositionButtonInCD, generalizationButtonInCD));
        GraphicsContext gc = classDiagramCanvas.getGraphicsContext2D();
        double scrollBarBreadth = 15.0;
        gc.getCanvas().setWidth(classDiagramScrollPane.getPrefWidth() - scrollBarBreadth);
        gc.getCanvas().setHeight(classDiagramScrollPane.getPrefHeight() - scrollBarBreadth);
        classDiagramDrawer = new ClassDiagramDrawer();
        classDiagramDrawer.setGraphicsContext(gc);
    }

    /**
     * <p> コードウィンドウを表示します </p>
     *
     * <p>
     *     同時に、コードウィンドウのコントローラクラスのインスタンスを取得しています。
     *     これはJavaFX仕様の取得方法です。
     * </p>
     *
     * <p>
     *     参照: <a href="http://hideoku.hatenablog.jp/entry/2013/06/07/205016"> FXML Controller で Stage を使うためのアレコレ - Java開発のんびり日記 </a>
     * </p>
     * @param mainController メインウィンドウのコントローラクラスのインスタンス
     * @param parent 親ウィンドウ
     * @param filePath ウィンドウFXMLファイルのパス
     * @param title ウィンドウのタイトル
     */
    public void showCodeStage(MainController mainController, Stage parent, String filePath, String title) {
        try {
            codeStage = new Stage();
            codeStage.initOwner(parent);
            codeStage.setTitle(title);
            FXMLLoader codeLoader = new FXMLLoader(getClass().getResource(filePath));
            codeStage.setScene(new Scene(codeLoader.load()));
            codeStage.show();
            codeController = codeLoader.getController();
            codeController.setMainController(mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUmlForCode(Package umlPackage) {
        if (umlPackage.getClasses().size() <= 0) return;

        classDiagramDrawer.changeDrawnNodeText(0, ContentType.Title, 0, umlPackage.getClasses().get(0).getName());
        classDiagramDrawer.allReDrawCanvas();
    }

    /**
     * <p> コードステージを取得します </p>
     *
     * <p>
     *     テストコードでのみの使用を想定していますが、開発が進むことで変わる可能性があります。
     * </p>
     *
     * @return コードステージ
     */
    Stage getCodeStage() {
        return codeStage;
    }

    /**
     * <p> Normalボタン選択時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるToolBar内のNormalボタンで参照 </p>
     */
    @FXML
    private void selectNormalInCD() {
        buttonsInCD = util.bindAllButtonsFalseWithout(buttonsInCD, normalButtonInCD);
    }

    /**
     * <p> Classボタン選択時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるToolBar内のClassボタンで参照 </p>
     */
    @FXML
    private void selectClassInCD() {
        buttonsInCD = util.bindAllButtonsFalseWithout(buttonsInCD, classButtonInCD);
    }

    /**
     * <p> Noteボタン選択時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるToolBar内のNoteボタンで参照 </p>
     */
    @FXML
    private void selectNoteInCD() {
        buttonsInCD = util.bindAllButtonsFalseWithout(buttonsInCD, noteButtonInCD);
    }

    /**
     * <p> Compositionボタン選択時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるToolBar内のCompositionボタンで参照 </p>
     */
    @FXML
    private void selectCompositionInCD() {
        buttonsInCD = util.bindAllButtonsFalseWithout(buttonsInCD, compositionButtonInCD);
    }

    /**
     * <p> Generalizationボタン選択時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるToolBar内のGeneralizationボタンで参照 </p>
     */
    @FXML
    private void selectGeneralizationInCD() {
        buttonsInCD = util.bindAllButtonsFalseWithout(buttonsInCD, generalizationButtonInCD);
    }

    /**
     * <p> Canvasクリック時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるScrollPane内のCanvas（ {@link #classDiagramCanvas} ）で参照 </p>
     */
    @FXML
    private void clickedCanvasInCD(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            clickedCanvasByPrimaryButtonInCD(event.getX(), event.getY());
        } else if (event.getButton() == MouseButton.SECONDARY) {
            clickedCanvasBySecondaryButtonInCD(event.getX(), event.getY());
        }
    }

    /**
     * <p> Canvasドラッグ時のシグナルハンドラ </p>
     *
     * <p> ClassDiagramTabにおけるScrollPane内のCanvas（ {@link #classDiagramCanvas} ）で参照 </p>
     */
    @FXML
    private void draggedCanvasInCD(MouseEvent event) {
        if (util.searchSelectedButtonIn(buttonsInCD) == normalButtonInCD &&
                classDiagramDrawer.isAlreadyDrawnAnyDiagram(event.getX(), event.getY())) {
            classDiagramDrawer.setMouseCoordinates(event.getX(), event.getY());
            classDiagramDrawer.moveTo(classDiagramDrawer.getNodeDiagramId(event.getX(), event.getY()), new Point2D(event.getX(), event.getY()));
            classDiagramDrawer.allReDrawCanvas();
        }
    }

    //
    // シグナルハンドラここまで
    //

    /**
     * <p> クラス図キャンバス上で（通常）左クリックした際に実行します </p>
     *
     * <p>
     * 操作ボタンにより動作が異なるが、通常操作以外は描画のみを行います。
     * また、通常操作時は何も動作しません。
     * </p>
     *
     * @param mouseX 左クリック時のマウス位置のX軸
     * @param mouseY 左クリック時のマウス位置のY軸
     */
    private void clickedCanvasByPrimaryButtonInCD(double mouseX, double mouseY) {
        if (classDiagramDrawer.isAlreadyDrawnAnyDiagram(mouseX, mouseY)) {
            if (util.searchSelectedButtonIn(buttonsInCD) == compositionButtonInCD) {
                if (!classDiagramDrawer.hasWaitedCorrectDrawnDiagram(ContentType.Composition, mouseX, mouseY)) {
                    classDiagramDrawer.setMouseCoordinates(mouseX, mouseY);
                    classDiagramDrawer.allReDrawCanvas();
                } else {
                    String compositionName = showCreateCompositionNameInputDialog();
                    classDiagramDrawer.addDrawnEdge(buttonsInCD, compositionName, mouseX, mouseY);
                    classDiagramDrawer.allReDrawCanvas();
                    convertUmlToCode();
                }
            } else if (util.searchSelectedButtonIn(buttonsInCD) == generalizationButtonInCD) {
                if (!classDiagramDrawer.hasWaitedCorrectDrawnDiagram(ContentType.Generalization, mouseX, mouseY)) {
                    classDiagramDrawer.setMouseCoordinates(mouseX, mouseY);
                    classDiagramDrawer.allReDrawCanvas();
                } else {
                    classDiagramDrawer.addDrawnEdge(buttonsInCD, "", mouseX, mouseY);
                    classDiagramDrawer.allReDrawCanvas();
                    convertUmlToCode();
                }
            }
        } else {
            classDiagramDrawer.setMouseCoordinates(mouseX, mouseY);
            if (util.searchSelectedButtonIn(buttonsInCD) == classButtonInCD) {
                String className = showCreateClassNameInputDialog();
                classDiagramDrawer.setNodeText(className);
                classDiagramDrawer.addDrawnNode(buttonsInCD);
                classDiagramDrawer.allReDrawCanvas();
                convertUmlToCode();
            } else if (util.searchSelectedButtonIn(buttonsInCD) == compositionButtonInCD) {
                classDiagramDrawer.resetNodeChosen(classDiagramDrawer.getCurrentNodeNumber());
                classDiagramDrawer.allReDrawCanvas();
            } else if (util.searchSelectedButtonIn(buttonsInCD) == generalizationButtonInCD) {
                classDiagramDrawer.resetNodeChosen(classDiagramDrawer.getCurrentNodeNumber());
                classDiagramDrawer.allReDrawCanvas();
            }
        }
    }

    private String showCreateClassNameInputDialog() {
        return showClassDiagramInputDialog("クラスの追加", "追加するクラスのクラス名を入力してください。", "");
    }

    private String showChangeClassNameInputDialog(String className) {
        return showClassDiagramInputDialog("クラス名の変更", "変更後のクラス名を入力してください。", className);
    }

    private String showAddClassAttributeInputDialog() {
        return showClassDiagramInputDialog("属性の追加", "追加する属性を入力してください。", "");
    }

    private String showChangeClassAttributeInputDialog(String attribute) {
        return showClassDiagramInputDialog("属性の変更", "変更後の属性を入力してください。", attribute);
    }

    private String showAddClassOperationInputDialog() {
        return showClassDiagramInputDialog("操作の追加", "追加する操作を入力してください。", "");
    }

    private String showChangeClassOperationInputDialog(String operation) {
        return showClassDiagramInputDialog("操作の変更", "変更後の操作を入力してください。", operation);
    }

    private String showCreateCompositionNameInputDialog() {
        return showClassDiagramInputDialog("コンポジションの追加", "コンポジション先の関連端名を入力してください。", "");
    }

    private String showChangeCompositionNameInputDialog(String composition) {
        return showClassDiagramInputDialog("コンポジションの変更", "変更後のコンポジション先の関連端名を入力してください。", composition);
    }

    /**
     * <p> クラス図のテキスト入力ダイアログを表示します </p>
     *
     * <p>
     * 入力ダイアログ表示中は、ダイアログ以外の本機能における他ウィンドウは入力を受付ません。
     * テキスト入力ダイアログを消去または入力を受付た場合は、他ウィンドウの入力受付を再開します。
     * </p>
     *
     * @param title      テキスト入力ダイアログのタイトル
     * @param headerText テキスト入力ダイアログのヘッダーテキスト
     * @return 入力された文字列 入力せずにOKボタンを押した場合やxボタンを押した場合は空文字を返します。
     */
    private String showClassDiagramInputDialog(String title, String headerText, String contentText) {
        classNameInputDialog = new TextInputDialog(contentText);
        classNameInputDialog.setTitle(title);
        classNameInputDialog.setHeaderText(headerText);
        Optional<String> result = classNameInputDialog.showAndWait();

        if (result.isPresent()) {
            return classNameInputDialog.getEditor().getText();
        } else {
            return "";
        }
    }

    /**
     * <p> クラス図キャンバス上で（通常）右クリックした際に実行します </p>
     *
     * <p>
     * 通常操作時のみ動作します。
     * メニュー表示を行うが、右クリックしたキャンバス上の位置により動作は異なります。
     * </p>
     *
     * @param mouseX 右クリック時のマウス位置のX軸
     * @param mouseY 右クリック時のマウス位置のY軸
     */
    private void clickedCanvasBySecondaryButtonInCD(double mouseX, double mouseY) {
        classDiagramScrollPane.setContextMenu(null);

        ContentType currentType = classDiagramDrawer.searchDrawnAnyDiagramType(mouseX, mouseY);

        if (currentType == ContentType.Undefined) return;
        if (util.searchSelectedButtonIn(buttonsInCD) != normalButtonInCD) return;

        if (currentType == ContentType.Class) {
            NodeDiagram nodeDiagram = classDiagramDrawer.findNodeDiagram(mouseX, mouseY);
            ContextMenu contextMenu = util.createClassContextMenuInCD(nodeDiagram.getNodeText(), nodeDiagram.getNodeType(),
                    classDiagramDrawer.getDrawnNodeTextList(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute),
                    classDiagramDrawer.getDrawnNodeTextList(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation),
                    classDiagramDrawer.getDrawnNodeContentsBooleanList(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute, ContentType.Indication),
                    classDiagramDrawer.getDrawnNodeContentsBooleanList(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation, ContentType.Indication));

            classDiagramScrollPane.setContextMenu(formatContextMenuInCD(contextMenu, nodeDiagram.getNodeType(), mouseX, mouseY));
        } else if (currentType == ContentType.Composition) {
            RelationshipAttributeGraphic relation = classDiagramDrawer.searchDrawnEdge(mouseX, mouseY);
            ContextMenu contextMenu = util.createClassContextMenuInCD(relation.getText(), relation.getType());
            classDiagramScrollPane.setContextMenu(formatContextMenuInCD(contextMenu, relation.getType(), mouseX, mouseY));

        } else if (currentType == ContentType.Generalization) {
            RelationshipAttributeGraphic relation = classDiagramDrawer.searchDrawnEdge(mouseX, mouseY);
            ContextMenu contextMenu = util.createClassContextMenuInCD("", relation.getType());
            classDiagramScrollPane.setContextMenu(formatContextMenuInCD(contextMenu, relation.getType(), mouseX, mouseY));
        }
    }

    /**
     * <p> UMLをコードに変換してコードエリアに反映します </p>
     */
    private void convertUmlToCode() {
        if (codeController != null) codeController.createCodeTabs(classDiagramDrawer.extractPackage());
    }

    /**
     * <p> クラス図キャンバス上での右クリックメニューの各メニューアイテムの動作を整形します </p>
     *
     * <p>
     * 名前の変更と内容の追加と内容の変更メニューではテキスト入力ダイアログを表示しますが、
     * それ以外ではメニューアイテムの選択直後にキャンバスを再描画します。
     * </p>
     *
     * @param contextMenu 右クリックメニューの見た目が整形済みの右クリックメニュー <br>
     *                    {@link UtilityJavaFXComponent#createClassContextMenuInCD(String, ContentType)} メソッドで取得したインスタンスを入れる必要がある。
     * @param type        右クリックした要素の種類
     * @return 動作整形済みの右クリックメニュー <br>
     * {@link UtilityJavaFXComponent} クラスで整形していないメニューや未分類の要素の種類を{@code contextMenu}や{@code type}に入れた場合は{@code null}を返す。
     */
    private ContextMenu formatContextMenuInCD(ContextMenu contextMenu, ContentType type, double mouseX, double mouseY) {
        if (type == ContentType.Class) {
            if (contextMenu.getItems().size() != 5) return null;
            contextMenu = formatClassContextMenuInCD(contextMenu);

        } else if (type == ContentType.Composition) {
            if (contextMenu.getItems().size() != 2) return null;
            contextMenu = formatCompositionContextMenuInCD(contextMenu, mouseX, mouseY);

        } else if (type == ContentType.Generalization) {
            if (contextMenu.getItems().size() != 1) return null;
            contextMenu = formatGeneralizationContextMenuInCD(contextMenu, mouseX, mouseY);

        } else {
            return null;
        }

        return contextMenu;
    }

    private ContextMenu formatClassContextMenuInCD(ContextMenu contextMenu) {
        // クラス名の変更
        contextMenu.getItems().get(0).setOnAction(event -> {
            String className = showChangeClassNameInputDialog(classDiagramDrawer.getNodes().get(classDiagramDrawer.getCurrentNodeNumber()).getNodeText());
            classDiagramDrawer.changeDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Title, 0, className);
            classDiagramDrawer.allReDrawCanvas();
            convertUmlToCode();
        });
        // クラスの削除
        contextMenu.getItems().get(1).setOnAction(event -> {
            classDiagramDrawer.deleteDrawnNode(classDiagramDrawer.getCurrentNodeNumber());
            classDiagramDrawer.allReDrawCanvas();
            convertUmlToCode();
        });
        // クラスの属性の追加
        ((Menu) contextMenu.getItems().get(3)).getItems().get(0).setOnAction(event -> {
            String addAttribute = showAddClassAttributeInputDialog();
            classDiagramDrawer.addDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute, addAttribute);
            classDiagramDrawer.allReDrawCanvas();
            convertUmlToCode();
        });
        // クラスの操作の追加
        ((Menu) contextMenu.getItems().get(4)).getItems().get(0).setOnAction(event -> {
            String addOperation = showAddClassOperationInputDialog();
            classDiagramDrawer.addDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation, addOperation);
            classDiagramDrawer.allReDrawCanvas();
            convertUmlToCode();
        });
        List<String> attributes = classDiagramDrawer.getDrawnNodeTextList(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute);
        List<String> operations = classDiagramDrawer.getDrawnNodeTextList(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation);
        // クラスの各属性の変更
        for (int i = 0; i < attributes.size(); i++) {
            int contentNumber = i;
            ((Menu) ((Menu) contextMenu.getItems().get(3)).getItems().get(1)).getItems().get(i).setOnAction(event -> {
                String changedAttribute = showChangeClassAttributeInputDialog(attributes.get(contentNumber));
                classDiagramDrawer.changeDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute, contentNumber, changedAttribute);
                classDiagramDrawer.allReDrawCanvas();
                convertUmlToCode();
            });
        }
        // クラスの各属性の削除
        for (int i = 0; i < attributes.size(); i++) {
            int contentNumber = i;
            ((Menu) ((Menu) contextMenu.getItems().get(3)).getItems().get(2)).getItems().get(i).setOnAction(event -> {
                classDiagramDrawer.deleteDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute, contentNumber);
                classDiagramDrawer.allReDrawCanvas();
                convertUmlToCode();
            });
        }
        // クラスの各属性の表示選択
        for (int i = 0; i < attributes.size(); i++) {
            int contentNumber = i;
            ((Menu) ((Menu) contextMenu.getItems().get(3)).getItems().get(3)).getItems().get(i).setOnAction(event -> {
                classDiagramDrawer.setDrawnNodeContentBoolean(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Attribute, ContentType.Indication, contentNumber,
                        ((CheckMenuItem) ((Menu) ((Menu) contextMenu.getItems().get(3)).getItems().get(3)).getItems().get(contentNumber)).isSelected());
                classDiagramDrawer.allReDrawCanvas();
            });
        }
        // クラスの各操作の変更
        for (int i = 0; i < operations.size(); i++) {
            int contentNumber = i;
            ((Menu) ((Menu) contextMenu.getItems().get(4)).getItems().get(1)).getItems().get(i).setOnAction(event -> {
                String changedOperation = showChangeClassOperationInputDialog(operations.get(contentNumber));
                classDiagramDrawer.changeDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation, contentNumber, changedOperation);
                classDiagramDrawer.allReDrawCanvas();
                convertUmlToCode();
            });
        }
        // クラスの各操作の削除
        for (int i = 0; i < operations.size(); i++) {
            int contentNumber = i;
            ((Menu) ((Menu) contextMenu.getItems().get(4)).getItems().get(2)).getItems().get(i).setOnAction(event -> {
                classDiagramDrawer.deleteDrawnNodeText(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation, contentNumber);
                classDiagramDrawer.allReDrawCanvas();
                convertUmlToCode();
            });
        }
        // クラスの各操作の表示選択
        for (int i = 0; i < operations.size(); i++) {
            int contentNumber = i;
            ((Menu) ((Menu) contextMenu.getItems().get(4)).getItems().get(3)).getItems().get(i).setOnAction(event -> {
                classDiagramDrawer.setDrawnNodeContentBoolean(classDiagramDrawer.getCurrentNodeNumber(), ContentType.Operation, ContentType.Indication, contentNumber,
                        ((CheckMenuItem) ((Menu) ((Menu) contextMenu.getItems().get(4)).getItems().get(3)).getItems().get(contentNumber)).isSelected());
                classDiagramDrawer.allReDrawCanvas();
            });
        }

        return contextMenu;
    }

    private ContextMenu formatCompositionContextMenuInCD(ContextMenu contextMenu, double mouseX, double mouseY) {
        // コンポジション関係の変更
        contextMenu.getItems().get(0).setOnAction(event -> {
            RelationshipAttributeGraphic composition = classDiagramDrawer.searchDrawnEdge(mouseX, mouseY);
            String compositionName = showChangeCompositionNameInputDialog(composition.getText());
            classDiagramDrawer.changeDrawnEdge(mouseX, mouseY, compositionName);
            classDiagramDrawer.allReDrawCanvas();
        });
        // コンポジション関係の削除
        contextMenu.getItems().get(1).setOnAction(event -> {
            classDiagramDrawer.deleteDrawnEdge(mouseX, mouseY);
            classDiagramDrawer.allReDrawCanvas();
        });

        return contextMenu;
    }

    private ContextMenu formatGeneralizationContextMenuInCD(ContextMenu contextMenu, double mouseX, double mouseY) {
        // 汎化関係の削除
        contextMenu.getItems().get(0).setOnAction(event -> {
            classDiagramDrawer.deleteDrawnEdge(mouseX, mouseY);
            classDiagramDrawer.allReDrawCanvas();
        });

        return contextMenu;
    }
}
