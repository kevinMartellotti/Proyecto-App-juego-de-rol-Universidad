package rol.malagacar.kevin.proyectorol;

import org.junit.Test;

import java.util.Random;

import rol.malagacar.kevin.proyectorol.ChatActivity;

import static org.junit.Assert.*;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    ChatActivity chatActivity = new ChatActivity();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dadoDaUnResultadoEntreElMinYElMax()
    {
        assertEquals(chatActivity.getRandomNumberInRange(2,8)<9 && chatActivity.getRandomNumberInRange(2,8)>1,true);
    }
/*
    @Test
    public void verifyInput() {
        EditText edit = findViewById(R.id.edit1);
        return edit.getText().toString().startsWith("0");
    }
*/
}