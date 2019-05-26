import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloWorld {
    public static void main(String[] args) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String date = df.format(new Date());
        System.out.println(date);

    }
}
