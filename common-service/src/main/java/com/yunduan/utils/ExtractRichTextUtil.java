package com.yunduan.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提取富文本中的内容
 * 返回文本内容
 */
public class ExtractRichTextUtil {


    /**
     * 正则预编译
     */
    private static final Pattern pattern = Pattern.compile("(<.+?>)|(</.+?>)");


    //一段富文本
    public static final String str = "<h1 class=\"ql-align-center\">《隐私政策》</h1><p><span style=\"color: rgb(64, 64, 64);\">本应用非常重视用户隐私政策并严格遵守相关的法律规定。请您仔细阅读《隐私政策》后再继续使用。如果您继续使用我们的服务，表示您已经充分阅读和理解我们协议的全部内容。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">本app尊重并保护所有使用服务用户的个人隐私权。为了给您提供更准确、更优质的服务，本应用会按照本隐私权政策的规定使用和披露您的个人信息。除本隐私权政策另有规定外，在未征得您事先许可的情况下，本应用不会将这些信息对外披露或向第三方提供。本应用会不时更新本隐私权政策。 您在同意本应用服务使用协议之时，即视为您已经同意本隐私权政策全部内容。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">1. 适用范围</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">(a) 在您注册本应用app帐号时，您根据app要求提供的个人注册信息；</span></p><p><span style=\"color: rgb(64, 64, 64);\">(b) 在您使用本应用网络服务，或访问本应用平台网页时，本应用自动接收并记录的您的浏览器和计算机上的信息，包括但不限于您的IP地址、浏览器的类型、使用的语言、访问日期和时间、软硬件特征信息及您需求的网页记录等数据；</span></p><p><span style=\"color: rgb(64, 64, 64);\">(c) 本应用通过合法途径从商业伙伴处取得的用户个人数据。</span></p><p><span style=\"color: rgb(64, 64, 64);\">(d)本应用严禁用户发布不良信息，如裸露、色情和亵渎内容，发布的内容我们会进行审核，一经发现不良信息，会禁用该用户的所有权限，予以封号处理。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">2. 信息使用</span></p><p><span style=\"color: rgb(64, 64, 64);\">(a)本应用不会向任何无关第三方提供、出售、出租、分享或交易您的个人登录信息。如果我们存储发生维修或升级，我们会事先发出推送消息来通知您，请您提前允许本应用消息通知。</span></p><p><span style=\"color: rgb(64, 64, 64);\">(b) 本应用亦不允许任何第三方以任何手段收集、编辑、出售或者无偿传播您的个人信息。任何本应用平台用户如从事上述活动，一经发现，本应用有权立即终止与该用户的服务协议。</span></p><p><span style=\"color: rgb(64, 64, 64);\">(c) 为服务用户的目的，本应用可能通过使用您的个人信息，向您提供您感兴趣的信息，包括但不限于向您发出产品和服务信息，或者与本应用合作伙伴共享信息以便他们向您发送有关其产品和服务的信息（后者需要您的事先同意）。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">3. 信息披露</span></p><p><span style=\"color: rgb(64, 64, 64);\">在如下情况下，本应用将依据您的个人意愿或法律的规定全部或部分的披露您的个人信息：</span></p><p><span style=\"color: rgb(64, 64, 64);\">(a) 未经您事先同意，我们不会向第三方披露；</span></p><p><span style=\"color: rgb(64, 64, 64);\">(b)为提供您所要求的产品和服务，而必须和第三方分享您的个人信息；</span></p><p><span style=\"color: rgb(64, 64, 64);\">(c) 根据法律的有关规定，或者行政或司法机构的要求，向第三方或者行政、司法机构披露；</span></p><p><span style=\"color: rgb(64, 64, 64);\">(d) 如您出现违反中国有关法律、法规或者本应用服务协议或相关规则的情况，需要向第三方披露；</span></p><p><span style=\"color: rgb(64, 64, 64);\">(e) 如您是适格的知识产权投诉人并已提起投诉，应被投诉人要求，向被投诉人披露，以便双方处理可能的权利纠纷；</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">4. 信息存储和交换</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">本应用收集的有关您的信息和资料将保存在本应用及（或）其关联公司的服务器上，这些信息和资料可能传送至您所在国家、地区或本应用收集信息和资料所在地的境外并在境外被访问、存储和展示。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">5. Cookie的使用</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">(a) 在您未拒绝接受cookies的情况下，本应用会在您的计算机上设定或取用cookies ，以便您能登录或使用依赖于cookies的本应用平台服务或功能。本应用使用cookies可为您提供更加周到的个性化服务，包括推广服务。</span></p><p><span style=\"color: rgb(64, 64, 64);\">(b) 您有权选择接受或拒绝接受cookies。您可以通过修改浏览器设置的方式拒绝接受cookies。但如果您选择拒绝接受cookies，则您可能无法登录或使用依赖于cookies的本应用网络服务或功能。</span></p><p><span style=\"color: rgb(64, 64, 64);\">(c) 通过本应用所设cookies所取得的有关信息，将适用本政策。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">6.本隐私政策的更改</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">(a)如果决定更改隐私政策，我们会在本政策中、本公司网站中以及我们认为适当的位置发布这些更改，以便您了解我们如何收集、使用您的个人信息，哪些人可以访问这些信息，以及在什么情况下我们会透露这些信息。</span></p><p><span style=\"color: rgb(64, 64, 64);\">(b)本公司保留随时修改本政策的权利，因此请经常查看。如对本政策作出重大更改，本公司会通过网站通知的形式告知。</span></p><p><span style=\"color: rgb(64, 64, 64);\">用户方如果披露自己的个人信息，如联络方式或者邮政地址，请您妥善保护自己的个人信息，仅在必要的情形下向他人提供。如您发现自己的个人信息泄密，尤其是本应用用户名及密码发生泄露，请您立即联络本应用客服，以便本应用采取相应措施。</span></p><p><span style=\"color: rgb(64, 64, 64);\">&nbsp;</span></p><p><span style=\"color: rgb(64, 64, 64);\">感谢您花时间了解我们的隐私政策！我们将尽全力保护您的个人信息和合法权益，再次感谢您的信任！</span></p>";



    /**
     * 去掉富文本标签
     * @param content 富文本
     * @return String
     */
    public static String dealContent(String content){
        // 匹配标签
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            // 替换图片
            content= matcher.replaceAll("").replaceAll("&nbsp;","").replaceAll(" ", "");
        }
        return content;
    }



    public static void main(String[] args) {
        String s = ExtractRichTextUtil.dealContent(str);
        System.out.println("s = " + s);
    }



}
