import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 95han on 2016/7/29.
 */
public class TranslateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        SelectionModel model = editor.getSelectionModel();
        String selectedText = model.getSelectedText();
        if (TextUtils.isEmpty(selectedText)) {
            return;
        }
        TranslateTask.start(selectedText, new TranslateTask.Callback() {
            @Override
            public void onSuccess(ResultBean resultBean) {
                System.out.println(resultBean.toString());
                StringBuffer targetText = new StringBuffer();
                targetText.append("<html>");
                targetText.append(resultBean.word);
                targetText.append("<br />");
                targetText.append(resultBean.custom);
                targetText.append("</html>");
                showPopupBalloon(editor, targetText.toString());
            }

            @Override
            public void onFailed(String errorMsg) {
                System.out.println(errorMsg);
                showPopupBalloon(editor, errorMsg);
            }
        });
    }

    private void showPopupBalloon(final Editor editor, final String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                JLabel jLabel = new JLabel(result);
                Color backgroundColor = new Color(255, 255, 255);
                Color borderColor = new Color(0, 0, 0, 30);
                Color transparentColor = new Color(0, 0, 0, 0);
                jLabel.setForeground(Color.BLACK);
                jLabel.setBackground(transparentColor);
                factory.createBalloonBuilder(jLabel)
                        .setBorderColor(borderColor)
                        .setFillColor(backgroundColor)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
//                factory.createHtmlTextBalloonBuilder(result, null, new Color(255, 255, 255), null)
//                        .createBalloon()
//                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
            }
        });
    }

}
