package me.kagura;

import javassist.*;

public final class JJsoup {

    protected static final Class vClass = initClass();

    /**
     * 生成一个Session对象
     *
     * @return
     */
    public static Session newSession() {
        return Session.newInstance(null);
    }

    /**
     * 用指定的key生成一个Session对象
     *
     * @param key
     * @return
     */
    public static Session newSession(String key) {
        return Session.newInstance(key);
    }

    /**
     * 从org.jsoup.helper.HttpConnection动态生成一个新class
     *
     * @return
     */
    private static synchronized Class initClass() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            String canonicalName = org.jsoup.helper.HttpConnection.class.getCanonicalName();
            //将org.jsoup.helper.HttpConnection复制一份org.jsoup.helper.HttpConnectionX
            CtClass ctxClass = classPool.getAndRename(canonicalName, canonicalName + "X");
            CtClass ctClassSession = classPool.getCtClass("me.kagura.Session");
            CtConstructor constructor = ctxClass.getDeclaredConstructors()[0];
            CtField ctfSession = new CtField(ctClassSession, "session", ctxClass);
            ctxClass.addField(ctfSession);
            //修改HttpConnectionX的无参构造方法为public
            constructor.setModifiers(Modifier.PUBLIC);
            constructor.addParameter(ctClassSession);
            //按照原逻辑初始化Request&Response，并将Session对象传入到HttpConnectionX内
            constructor.setBody("{" +
                    "this.req = new org.jsoup.helper.HttpConnection.Request();" +
                    "this.res = new org.jsoup.helper.HttpConnection.Response();" +
                    "this.session=$1;" +
                    "}");
            //删除静态方法connect
            for (CtMethod method : ctxClass.getDeclaredMethods("connect")) {
                ctxClass.removeMethod(method);
            }
            //修改原execute方法，在执行前判断Content-Type是否为JSON
            CtMethod declaredMethod = ctxClass.getDeclaredMethod("execute");
            declaredMethod.insertBefore("{" +
                    "   if (this.req.method().hasBody() && this.req.requestBody() != null){" +
                    "       try{" +
                    "           com.alibaba.fastjson.JSON.parse(this.req.requestBody());" +
                    "           this.req.header(CONTENT_TYPE, \"application/json;charset=\" + this.req.postDataCharset());" +
                    "       }catch (Exception e){}" +
                    "   }" +
                    "}");
            //修改原execute方法，在返回前将cookies存入Session对象
            declaredMethod.insertAfter("{session.cookies($_.cookies());}");
            return ctxClass.toClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
