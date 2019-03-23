package me.kagura;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class JJsoupTests {

    @Test
    public void TestConnect() throws IOException {
        Session session = JJsoup.newSession();
        session.connect("https://github.com/KingFalse/jjsoup").execute();
        session.cookies().forEach((k, v) -> System.err.println(k + "    " + v));
    }

    @Test
    @Before
    public void TestSerializeSession() throws IOException {
        Session session = JJsoup.newSession();
        session.ext.put("name", "Kagura");
        session.ext.put("url", "https://kagura.me");

        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(new File("session.txt")));
        oo.writeObject(session);
        oo.flush();
        oo.close();
    }

    @Test
    public void TestDeserializeSession() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("session.txt")));
        Session session = (Session) ois.readObject();
        System.err.println(session.ext.get("name"));
        System.err.println(session.ext.get("url"));
    }

}
