package com.wzq.hadoop.regular;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * <b>正则表达式 java.util.regex</b>
 * <p>
 * <a href="https://www.yiibai.com/java/java_regular_expressions.html">https://www.yiibai.com/java/java_regular_expressions.html</a>
 * <p>
 * <a href="https://www.runoob.com/regexp/regexp-syntax.html">https://www.runoob.com/regexp/regexp-syntax.html</a>
 *
 *  <ul>
 *     <li>
 *         {@link Pattern} => Pattern对象是正则表达式的编译表示。Pattern类不提供公共构造函数。要创建模式，
 *         首先需要调用它的公共静态{@code compile()}方法，然后返回Pattern对象。这个方法接收正则表达式作为第一个参数
 *     </li>
 *     <li>
 *         {@link Matcher} => Matcher对象是解释模型并对输入字符串执行匹配操作的引擎。与Pattern类一样，
 *         Matcher没有定义公共的构造函数。通过{@code Pattern.matcher()}方法获取Matcher对象
 *     </li>
 *     <li>
 *          {@link PatternSyntaxException} => 该对象是未经检查的异常，指示正则表达式中的语法错误
 *     </li>
 * </ul>
 */
public class RegexMatches {
    public static void main(String[] args) {
        String line = "\\dir\\${user.name}";
        String pattern = "\\$\\{[^\\}\\$\u0020]+\\}";

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(line);

        while (matcher.find()) {
            System.out.println(matcher.group());
        }

    }

    private static void test1() {
        String line = "This order was placed for QT3000! OK?";
        String pattern = "(.*)(\\d+)(.*)";

        Pattern r = Pattern.compile(pattern);

        Matcher matcher = r.matcher(line);
        if (matcher.find()) {
            System.out.println("Found value: " + matcher.group(0));
            System.out.println("Found value: " + matcher.group(1));
            System.out.println("Found value: " + matcher.group(2));
        } else {
            System.out.println("NO MATCH");
        }
    }
}
