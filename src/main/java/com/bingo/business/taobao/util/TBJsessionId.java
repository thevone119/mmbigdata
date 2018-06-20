package com.bingo.business.taobao.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *淘宝数据抓取的cookie反刷机制破解
 * 每次访问淘宝必须带JSESSIONID，而且10秒内同1个JSESSIONID不能请求2次。
 * isg应该是淘宝的校验位，传错了不能访问的。
 * Created by huangtw on 2018-04-06.
 */
public class TBJsessionId {

    private static long lastCookieModify = 0L;
    private static String myiecookie = "";
    private static String[] jsessids = {
            "JSESSIONID=CFBEF34B4980E0CB774CD05A2C3F8F33; isg=BHV1IH-UQ_IIg6c43DqMJ21ShPHvWgJuXAvqIfeaMew7zpXAv0I51INMHJJ4lUG8",
            "JSESSIONID=72BE13D8FB49DB575F0F68D5441DC953; isg=BLi415yEFt1CVXovGWmZWJDRiWaKiTfB0SgXVvIpBPOmDVj3mjHsO84vwQW9RtSD",
            "JSESSIONID=548A52DCCF600050223BD7E83626CD25; isg=BMTEsw9kQnE2yvZLNSUtRMTVlUJ2dcMtBayb8t5lUA9SCWTTBu241_qrTaHRCiCf",
            "JSESSIONID=05A2328B6B1C23E356AC3118501F3339; isg=BISEc1ecgrF2KjYL9WXtBASVVQK2NYNtxWzbMp4lEM8SySSTxq14l7prDWERSuBf",
            "JSESSIONID=53756101A28CF4B2E8B2430CB6B2A1B7; isg=BDQ0Y_750qEmjkYbxfVdFDQFBfJmpXOdNZwLQs6VwL9COdSD9h0oh-r7vXHh2pBP",
            "JSESSIONID=C51D85B21EAEF35126D0E792D9AE306D; isg=BFxc6zVMaqlevB6DXT01DBxdLXrOfSuVLdTzujZdaMcqgfwLXuXQj9Iz5el5EjhX",
            "JSESSIONID=C0B23FC7C49A08FAC7000B86870F92B5; isg=BFtbbkpKhSQ1k_kmhrgKsadw6r8FmERYxunUn02YN9pxLHsO1QD_gnmuwoyiF8cq",
            "JSESSIONID=1143144B41483CCB15B601ADA93BFF44; isg=BHR0oxmEkmH6pAZbBbWdVPRFRTIm5bNdddzLAg7VAP-CeRTDNl1oxyo7_bGhmtCP",
            "JSESSIONID=6003A8B00E9B26A3B07B2E27836D87C1; isg=BMLCueEOLFvZgTBxhzszHtbjE8jkO-3H1zpdqAzb7jXgX2LZ9CMWvUidC1sjFD5F",
            "JSESSIONID=B38860C37284670196FB75549E7081CB; isg=BCAgn7FgbqUna9LX8VGxkLip8S4ygS-ZCaC_jpox7DvOlcC_QjnUg_anKT0VPrzL",
            "JSESSIONID=655C2A6B6600385A6B7EE20E15B27C40; isg=BCgoh-785g1vDsr_KTkJKIAB-Rb6-acxwRiHpuJZdKOWPcinimFc675_MdXNFkQz",
            "JSESSIONID=36899581D90C23B3138EC9F5007D6804; isg=BKamDf2LwPefFpQle8_vAhKv9xzoL8ErE2b53JBPkkmkE0Yt-Bc6UYzhbw-foOJZ",
            "JSESSIONID=1FCB81507B97D7CDC0BC636056D3EBF5; isg=BPHxrNBCD0YLDaMk-MZw04GGAH1Ldk46oP_uHdMG7bjX-hFMGy51IJ8YGI6cKf2I",
            "JSESSIONID=5E02BC51A2DEBA0356646F8D1E6844BF; isg=BDk51HSkR-7z9xsMcG4Iq4m-SKXT7gaSuJe29VtutWDf4ll0o5Y9yKcwYOYUwcUw",
            "JSESSIONID=2181CAD0329C496A1EEE93280673B704; isg=BENDtvWVHWx1LdFu3iCiac9I0gctEPyQvuH8V3UgW6IZNGNW_YhnSiHtqsR6uC_y",
            "JSESSIONID=647D164549ABAAC9EEABB147B86C4BA4; isg=BBUVQFWs45J3BscYvBpsB42yJBEPuuJOPGtKAZe60Qzb7jXgX2LZ9COsvPLYdeHc",
            "JSESSIONID=6580C210048196574D7E3E9B7D646CAF; isg=BODgX-EHruViORKXsZFxUPhpse5yQe_ZyWD_zlrxrPuOVYB_AvmUQ7Zn6f1VfnyL",
            "JSESSIONID=17BDB518BBA4D2545B7E620228871E51; isg=BPj4Fx401p0aIzpvWSnZmFARyaZKyXeBEWjXFjJpRDPmTZg32nEsew5vAUV9BhTD",
            "JSESSIONID=A28FF2FACD38B9D3003FA2CC901BF291; isg=BFxc6-8CaqlGzB6DXT01DBxdLXrOfSuVLdTzujZdaMcqgfwLXuXQj9Iz5el5EjhX",
            "JSESSIONID=D392175E0C7D95894643484B6443DF6D; isg=BBERTMoEL-brFUOE2CZQs6FmIB1r1q6agF_OffOmDVj3mjHsO86VwL_4OG58iR0o",
            "JSESSIONID=40EC876AD07C740A1C12BC815BF76A2B; isg=BE5OFcYUmH8gyywtE3fH-vpnnyTQZzmjiz4h1HiXutEM2-414F9i2fSZFwe3WArh",
            "JSESSIONID=CEDC23E712DB53116951C3637462FB2A; isg=BGhoxyvMps0qPoo_aflJaEBBOVa6OefxAVhHZiKZtOPWfQjnyqGcK_6_cRWN1oRz",
            "JSESSIONID=C7DFF05BAE30EE30B7C7D6C11782DC0A; isg=BP7-BfYAKG_wh3w9Ywe3iqqXTxSAl-nT-66R5KgHasE8S54lEM8SySQpxxeH6LrR",
            "JSESSIONID=2032C40AC66280A8D6D2A01F32DF6C0D; isg=BDEx7NYpzwbKVWNkOIawE0HGQL0Lto764D-u3RNGLfgXOlGMW261YN9YWM5c6T3I",
            "JSESSIONID=C934C7980183D21B0AC68E0F7621D91B; isg=BHd3Gh-G4YhifWVyAiSuTVuEBmsBlGD0ir0oa8kkk8ateJe60Qzb7jVaXtgmiyMW",
            "JSESSIONID=840B6EED8DDCDF9889E72A44A6E162AC; isg=BL-_QqhW2TBkmt1a-sxmJWO8TpOJDDgsglXwQ1GMW261YN_iWXSjlj1ypjCeI-u-",
            "JSESSIONID=B03434F191F1D2A0AE1A0865B7EC372A; isg=BGZmzbhLADfVNdTlOw-vwlJvt9wo74Fr0yY5HFAPUglk0wbtuNf6EUyhL8_f4KIZ",
            "JSESSIONID=4F95CF1027117EEB564BB494C3C738B8; isg=BCsr_vaU9bTQWimWNqjaIXdAuk_V6BSoFvnkD52oB2rBPEueJRDPEsmeknxSB5e6",
            "JSESSIONID=86A48EC6DF8646A90C560DF09AB1DFD4; isg=BM_PEpGYyQDUl80Kanx21fPMXmMZXAh8EuXAc-Hcaz5FsO-y6cSzZs2ituAO0_uO",
            "JSESSIONID=2B58F2269B5C75B82A62D2BA68066763; isg=BMfHKuJQUZif09ViMpT-vasUVntRZLDEWg34W5m049Z9COfKoZwr_gXKrshW-3Mm",
            "JSESSIONID=40D4BED17E95403E2A5DC5EB10158097; isg=BJaWPU5L0Kcm_uR1ix-fUoIf50xYn_Ebg9bprAD_gnkUwzZdaMcqgfyxX18v8NKJ",
            "JSESSIONID=4F2C74D8FBD9188ABD26E6D143D17EC9; isg=BENDtq_bHWx7W9Fu3iCiac9I0gctEPyQvuH8V3Ugn6IZNGNW_YhnSiGmqsR6jy_y",
            "JSESSIONID=7FB2C46BABB0FAEAB83F505B3A4AE10C; isg=BMDAv5q9jkWMJHI3UTER8NiJkU5S4Y956YAfbjpRjFtutWDf4ll0o5aHyR11Hlzr",
            "JSESSIONID=5E4B3BBD23598CB448273FFAF8C76DD7; isg=BOHh3CRmH3aV77N0CBZgI3F28K07Jn7q0K8e7UO23ehHqgF8i95lUA_oCN4see24",
            "JSESSIONID=D621905510ED6432E99BE7973D06B5FB; isg=BLS047a1UiGw0MabRXXdlLSFhXLmJfMdtRyLwk4VQD_CuVQDdp2oB2p7PfFhWhDP",
            "JSESSIONID=AE03D6A542A268386D58E0524DC5036E; isg=BIyMW2XvOhmY2y4TLU1lnExNXep-bRsFncSjSuZNmDfacSx7DtUA_4LDFXnJImjH",
            "JSESSIONID=70744143B00446099BF63E080D5C1AB9; isg=BH19CNhk-1pRbV_AFAIEv7UKjNm3spoGFOPyOT_CuVQDdp2oB2rBPEskJKowbckk",
            "JSESSIONID=8D5C5A259B5655E4E41EF2E7FE9E7E8A; isg=BHR0o8yskmHwwwZbBbWdVPRFRTIm5bNdddzLAg7VAP-CeRTDNl1oxyo7_bGhmtCP",
            "JSESSIONID=E0D0B0EF13296A94CB72227DF8279AC0; isg=BHZ2nQFzMAdG-8SVq7-_8uK_x6y4P5G7o3YJTOBfYtn0Ixa9SCcK4dzRP_9PkLLp",
            "JSESSIONID=90ABAD7F3A306C677B765E7585C7816D; isg=BN7eZYQXCM8ebFxdA6dXKoo3L3TgN4lzm86xhIhnSiEcq36F8C_yKQRJp7eniJox",
            "JSESSIONID=88E19AB7BB22364149F29F607B1F9D18; isg=BCoqgdbhBCOqQIg5XyMrVv57e5AMM4WfD7LF4LTj1n0I58qhnCv-BXAVcxN7DCaN",
            "JSESSIONID=61DD87ADA1EAF076FBA818E137A0E027; isg=BCoqgWCCBCOqbYg5XyMrVv57e5AMM4WfD7LF4LTj1n0I58qhnCv-BXAVcxN7DCaN",
            "JSESSIONID=4E62A4B5D9BAA12F77F83B88AB96777F; isg=BMzMm9YA-tlYbe5TbQ2l3AyNnSo-rVvF3QRjCiaN2HcasWy7ThVAP8IDVbmJ4qgH",
            "JSESSIONID=E9DA7D977C1477CD8055FB3FD0295602; isg=BMbGrdFE4Bd32bSFWy_P4jIPF7yIjyGLc8bZPLDvsunEs2bNGLda8axBj-9_AAL5",
            "JSESSIONID=D5FA78CDFC6181F05F93F029D0A077A7; isg=BDY2XRVpcEeHYARVa_9_siJ_h2z4_1H7YzZJjKAfIpm049Z9COfKoZyR_7-P0HKp",
            "JSESSIONID=7B15906AD710539D8AD530FF0C360C9E; isg=BP__gtcjmfAmFJ2aOoymZSP8jtNJTHjswpWwA5HMm671oB8imbTj1n2y5nBe4yv-",
            "JSESSIONID=12ABEFD821AEDE971A8084667CBF36AC; isg=BNXVAMzgI9K4ugfYfFosx81y5NFPeqKO_CuKQVd6kcybrvWgHyKZtONsfLIYtaGc",
            "JSESSIONID=567BF51E8E55DE2C294CA8D6C6B4EA20; isg=BIKCeYmobJsTXXAxR3vz3haj04gk-60Hl_qd6MybrvWgHyKZtOPWfQhdyxtjVP4F",
            "JSESSIONID=D7B0B364367C08379DE665E9ECD1E5C6; isg=BP39iErte9rQ199AlIKEPzWKDFk3MhqGlGNyub9COdSD9h0oh-pBvMukpCqw7Umk",
            "JSESSIONID=1712B790D1FFC88B0C40932F8734715B; isg=BP__gtPsmfAm352aOoymZSP8jtNJTHjswpWwA5HMm671oB8imbTj1n2y5nBe4yv-",
            "JSESSIONID=5EC3F925CE718FD9A2426295BAC7C8B3; isg=BJmZtKs0J86cD_sskI6oS2leqIWzjqay2LdWFbtOFUA_wrlUA3adqAfQwIa04SUQ",
            "JSESSIONID=6CFA72C9AB851947767A2DEB7145CC91; isg=BH5-hVDPqO6cU_y944c3CioXz5QAF2lTey4RZCiH6kG8yx6lkE-SSaSJR5cHaDpR",
            "JSESSIONID=937D3FC77B8E22E465DF8BEED45A2EBC; isg=BDAwbxmqHvR--cKHYQGBQMj5Af5C0RWlmfCPviqB_Ate5dCP0onkU4b3Oe2F7syb",
            "JSESSIONID=6FEE245B575F75255125D519FD43F09B; isg=BCsr_tvT9bUxZimWNqjaIXdAuk_V6D6kFvnkD52oB2rBPEueJRDPEsm-knxSB5e6",
            "JSESSIONID=1962D69037066A1697F76DC4B819378B; isg=BICAf637zoQv97L3EXHRsBhJUQ6SoWW1qUBfrvoRTBsudSCfohk0Y1Znid21Xhyr",
            "JSESSIONID=D1AA517133DC02A71FE99F7AD76BDEDF; isg=BOPj1gVJvQ04q3FOvgACSW-ocidNcHZ8HsFcNxVAP8K5VAN2nagHasGmSiTab88S",
            "JSESSIONID=33F4CF29EBCD28AF31BF1C0C6C41A4A6; isg=BLGxbBvZT4emlePkuAYwk8FGwD2LNiR2YL8uXZPGrXiXutEM2-414F_42E7cab1I",
            "JSESSIONID=CA4521F8CCEC6C299CD96F074277749A; isg=BJ-folkiOZFk7j36mmwGRUPcLvPprPLAovUQ4zHsO86VwL9COdSD9h2yhlC-w8se",
            "JSESSIONID=182E3193A1C24BE1A71B110910E47C2F; isg=BG1tOOs8ywsCLq8QpFI0jyX6fAknYqD6hFPiCa9yqYRzJo3YdxqxbLvUFPrAvblU",
            "JSESSIONID=ADEEA4CCF43D9B2723C77DB570069370; isg=BLu7ThoSZQUjwlnGJtiqUQcQSp_luM50Zol0v614l7rRDNvuNeBfYtluIqxCNycK",
            "JSESSIONID=E4A3614151BB9FEFDC944A47906DF73C; isg=BLe3WhuEoUnPNyWyQuTujRvERqvB1Iq4yv3oKwlk0wbtuNf6EUwbLnW6nhjmS2NW",
            "JSESSIONID=86ADCAD7AC44D8DCD09562E21C3805C0; isg=BBgYt3QP9jwUk9rPuYk5eHDx6UZqqX3tcci3dlIJZNMG7bjX-hFMGy7vISVdZjRj",
            "JSESSIONID=781A7DE15487DBF75AD6843E20C71843; isg=BE9PkqXySYG3G02K6vz2VXNM3uOZ3KLwkmVA82Fc677FMG8yaUQz5k0CNmCOU3sO",
            "JSESSIONID=6F69CE814BACA6B5D9CEE1CA5C920D1D; isg=BKCgH5un7iRN51JXcdExEDgpca6yAYUViSA_DhqxbLtOFUA_wrlUA3YHqb2VvjxL",
            "JSESSIONID=FFA3F62CF369DE001108F8288EA0FDD2; isg=BP__goQFmfHGwp2aOoymZSP8jtNJTFLgwpWwA5HMm671oB8imbTj1n2S5nBe4yv-",
            "JSESSIONID=FB86F06B6F3A1505B3ADFF124094C351; isg=BFZW_RlJEOaHEyQ1S19fEsLfpwyYX5tXQ5Yp7MC_QjnUg_YdKIfqQbxRHx9vMJJJ",
            "JSESSIONID=77CF98C06DFFDFFDD8959C0D7BBD90A0; isg=BMbGrWp84BaXF7SFWy_P4jIPF7yIjwuHc8bZPLDvsunEs2bNGLda8axhj-9_AAL5",
            "JSESSIONID=77CF98C06DFFDFFDD8959C0D7BBD90A0; isg=BDw8S6H8SgiJKH4jfd3VrHz9DdouneE5zXQTWha9SCcK4dxrPkWw77JzxQmZshi3",
            "JSESSIONID=CCD426B438FD786E7454BA3BD30E4640; isg=BCkpBG8VV59sa2tcgL44-3luONVDXhxOKEemxcsepZBPkkmkE0Yt-BegUDakEbVg",
            "JSESSIONID=6FC22D6B93312A6AF41D467120254381; isg=BD4-RWIo6C5Yujx9o8f3ymrXj1RA1wOfO-5RpOhHqgF8i95lUA9SCWRJB1dHqPoR",
            "JSESSIONID=1228B27AC75C9C96A9ABB7ED511EE212; isg=BMTEswcgQnDG6fZLNSUtRMTVlUJ2dekhBayb8t5lUA9SCWTTBu241_qLTaHRCiCf",
            "JSESSIONID=F0A289BCA7A8C02C59594BD48B558A97; isg=BJmZtI9JJ8977PsskI6oS2leqIWzjoy-2LdWFbtOFUA_wrlUA3adqAfwwIa04SUQ",
            "JSESSIONID=5793EAE31980DED01CA384DE1FF2C60E; isg=BEhIJ9njhqyifOpfCZlpiKDhGbaaWa2dIfhnBgL5lEO23ehHqgF8i97_UbWtdmTT",
            "JSESSIONID=45C959A9A385289483D818E6C409DF0C; isg=BHJyqbVPfEpEMEAB18vjLoYTw7iUq3f7B6qNuDxLniUQzxLJJJPGrXgNu2vzpO41",
            "JSESSIONID=7A909E3CA0038CDEE97E6F4E0922DFD6; isg=BNTUg01PckB2HOZ7pdU9dFRlpRKGhflxlfxrIm61YN_iWXSjlj3Ip4r7XVFBujBv",
            "JSESSIONID=14679196AFCF1B438F946A5D2C013A30; isg=BLOzZnti7Z1tw6E-7vDSOT-4QrcdQEbMrtFsp2VQD1IJZNMG7bjX-hHWGpSKX5-i",
            "JSESSIONID=3D50AE2B493474D6F11F0D6D8327CD4B; isg=BENDtnojHW2dA9Fu3iCiac9I0gctENacvuH8V3Ugn6IZNGNW_YhnSiGGqsR6jy_y",
            "JSESSIONID=4AA1F63262FF01357F2A7D55097DB8E9; isg=BIaGbc9TIFZQAvRFG2-PonLP13zIT8vHM4YZfHCvcqmEcyaN2HcasWwhT6-_QMK5",
            "JSESSIONID=FA8F559D47CBC811E9D4AB060854CA91; isg=BDU14A2_gzO_vOf4nHpM560SRLEvGuiiHMsqYbda8az7jlWAfwL5lEMs3FK41QF8",
            "JSESSIONID=851736287C1DD338BAF25BDA64A967B7; isg=BIyMW0PoOhh-5S4TLU1lnExNXep-bTEJncSjSuZNmDfacSx7DtUA_4LjFXnJImjH",
            "JSESSIONID=23C67698BE2F58FD9667D67468B78061; isg=BJSUQ6dqsoA2zyY7ZRX9NJQlZdLGRbmxVbyrYi51IJ-iGTRjVv2IZ0q7HRGB-vAv"
    };

    private static List<String> cookiesList = new ArrayList<>();
    private static int cookiesidx=0;

    private static int curridx = 0;

    public static synchronized String getJsessionId(){
        curridx++;
        if(curridx>=jsessids.length){
            curridx = 0;
        }
        return jsessids[curridx];
        //String uuid= UUID.randomUUID().toString().replace("-", "").toUpperCase();
        //return "JSESSIONID="+uuid+"; isg=BDEx7NYpzwbKVWNkOIawE0HGQL0Lto764D-u3RNGLfgXOlGMW261YN9YWM5c6T3I";
    }


    /**
     * 获取当前ie的cooike
     * @return
     */
    public static synchronized String getCurrIeCooike() throws IOException {
        File f = new File("d:/myIecooike");
        if(f.lastModified()==lastCookieModify){
            return myiecookie;
        }
        lastCookieModify = f.lastModified();
        myiecookie = org.apache.commons.io.IOUtils.toString(new FileInputStream(f));
        return myiecookie;
    }

    public static synchronized String getNextIeCooike(String path) throws IOException {
        if(cookiesList.size()==0){
            //加载cookies
            File f = new File(path);
            File[] fl = f.listFiles();
            for(File _f:fl){
                String cookie = org.apache.commons.io.IOUtils.toString(new FileInputStream(_f));
                cookie = cookie.substring(cookie.indexOf("isg="),cookie.indexOf("isg=")+69);
                cookiesList.add(cookie);
            }
        }
        if(cookiesidx>=cookiesList.size()){
            cookiesidx=0;
        }
        return cookiesList.get(cookiesidx++);
    }


    public static void main(String str[]) throws IOException {
        //System.out.println(getNextIeCooike("D:\\mycooike"));
        //Pattern pattern = Pattern.compile("<div class=\"detail\">.*title=\"(.*?)\"href =\"(.*?)\" target.*<span>(.*?)</span>.*\"sale-count\">(.*?)</span>.*<div>",Pattern.DOTALL);
        //String rexstr = "<div class=\"detail\">.*title=\"(.*?)\".*href=.*id=(.*?)\".*<span>(.*?)</span>.*count\">(.*?)</span>.*</div>.*";
        String rexstr = "<div class=\"detail.*?title=\"(.*?)\".*?id=(.*?)\".*?<span>(.*?)</span>.*?sale-count\">(.*?)</span>";
        Pattern pattern = Pattern.compile(rexstr, Pattern.DOTALL);

        Matcher matcher = pattern.matcher("<div class=\"detail\">\n" +
                "\t\t\t\t\t\t\t<p class=\"desc\"><a  title=\"运费补差，各种补差\" href=\"//item.taobao.com/item.htm?id=532633723777\" target=\"_blank\">运费补差，各种补差</a></p>\n" +
                "\t\t\t\t\t\t\t<p class=\"price\">￥<span>1.00</span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p class=\"sale\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t已售出<span class=\"sale-count\">522</span>件\n" +
                "\t\t\t\t\t\t\t\t                            </p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li  class=\"item odd \">\n" +
                "\t\t\t\t\t\t<div class=\"ranking\">\n" +
                "\t\t\t\t\t\t\t<span>2</span>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t        \t\t\t\t\t\t\t\t\t\t<div class=\"more\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=530983067594\" target=\"_blank\"><img src=\"//img.alicdn.com/bao/uploaded/i2/672177/TB22D8cbCmK.eBjSZPfXXce2pXa_!!672177.jpg_120x120.jpg\" class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"img\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=530983067594\" target=\"_blank\"><img alt=\"商品图片\"  data-ks-lazyload=\"//img.alicdn.com/bao/uploaded/i2/672177/TB22D8cbCmK.eBjSZPfXXce2pXa_!!672177.jpg_40x40.jpg\" src=\"//assets.alicdn.com/s.gif\"  class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"detail\">\n" +
                "\t\t\t\t\t\t\t<p class=\"desc\"><a  title=\"水族5-12V超静音微型小型气机鱼缸USB增氧泵充电宝钓鱼车载便携式\" href=\"//item.taobao.com/item.htm?id=530983067594\" target=\"_blank\">水族5-12V超静音微型小型气机鱼缸USB增氧泵充电宝钓鱼车载便携式</a></p>\n" +
                "\t\t\t\t\t\t\t<p class=\"price\">￥<span>1.00</span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p class=\"sale\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t已售出<span class=\"sale-count\">192</span>件\n" +
                "\t\t\t\t\t\t\t\t                            </p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li class=\"item even \">\n" +
                "\t\t\t\t\t\t<div class=\"ranking\">\n" +
                "\t\t\t\t\t\t\t<span>3</span>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t        \t\t\t\t\t\t\t\t\t\t<div class=\"more\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=540344813937\" target=\"_blank\"><img src=\"//img.alicdn.com/bao/uploaded/i1/672177/TB2C_5fbxsIL1JjSZFqXXceCpXa_!!672177.jpg_120x120.jpg\" class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"img\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=540344813937\" target=\"_blank\"><img alt=\"商品图片\"  data-ks-lazyload=\"//img.alicdn.com/bao/uploaded/i1/672177/TB2C_5fbxsIL1JjSZFqXXceCpXa_!!672177.jpg_40x40.jpg\" src=\"//assets.alicdn.com/s.gif\"  class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"detail\">\n" +
                "\t\t\t\t\t\t\t<p class=\"desc\"><a  title=\"钓鱼USB增氧泵超静音微型小型打氧机鱼缸充电宝用冲氧泵水族鱼缸\" href=\"//item.taobao.com/item.htm?id=540344813937\" target=\"_blank\">钓鱼USB增氧泵超静音微型小型打氧机鱼缸充电宝用冲氧泵水族鱼缸</a></p>\n" +
                "\t\t\t\t\t\t\t<p class=\"price\">￥<span>15.99</span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p class=\"sale\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t已售出<span class=\"sale-count\">134</span>件\n" +
                "\t\t\t\t\t\t\t\t                            </p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li  class=\"item odd \">\n" +
                "\t\t\t\t\t\t<div class=\"ranking\">\n" +
                "\t\t\t\t\t\t\t<span>4</span>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t        \t\t\t\t\t\t\t\t\t\t<div class=\"more\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=558884956791\" target=\"_blank\"><img src=\"//img.alicdn.com/bao/uploaded/i1/672177/TB2C_5fbxsIL1JjSZFqXXceCpXa_!!672177.jpg_120x120.jpg\" class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"img\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=558884956791\" target=\"_blank\"><img alt=\"商品图片\"  data-ks-lazyload=\"//img.alicdn.com/bao/uploaded/i1/672177/TB2C_5fbxsIL1JjSZFqXXceCpXa_!!672177.jpg_40x40.jpg\" src=\"//assets.alicdn.com/s.gif\"  class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"detail\">\n" +
                "\t\t\t\t\t\t\t<p class=\"desc\"><a  title=\"超静音USB增氧泵钓鱼水族微型气泵打氧冲氧机车载便携钓鱼充电宝\" href=\"//item.taobao.com/item.htm?id=558884956791\" target=\"_blank\">超静音USB增氧泵钓鱼水族微型气泵打氧冲氧机车载便携钓鱼充电宝</a></p>\n" +
                "\t\t\t\t\t\t\t<p class=\"price\">￥<span>15.99</span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p class=\"sale\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t已售出<span class=\"sale-count\">81</span>件\n" +
                "\t\t\t\t\t\t\t\t                            </p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<li class=\"item even last\">\n" +
                "\t\t\t\t\t\t<div class=\"ranking\">\n" +
                "\t\t\t\t\t\t\t<span>5</span>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t        \t\t\t\t\t\t\t\t\t\t<div class=\"more\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=536484810339\" target=\"_blank\"><img src=\"//img.alicdn.com/bao/uploaded/i1/672177/TB2emJatVXXXXXhXFXXXXXXXXXX_!!672177.jpg_120x120.jpg\" class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"img\">\n" +
                "\t\t\t\t\t\t\t<a  href=\"//item.taobao.com/item.htm?id=536484810339\" target=\"_blank\"><img alt=\"商品图片\"  data-ks-lazyload=\"//img.alicdn.com/bao/uploaded/i1/672177/TB2emJatVXXXXXhXFXXXXXXXXXX_!!672177.jpg_40x40.jpg\" src=\"//assets.alicdn.com/s.gif\"  class=\"hover-show\"></a>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t<div class=\"detail\">\n" +
                "\t\t\t\t\t\t\t<p class=\"desc\"><a  title=\"水族硅胶气管4*6MM增氧管冲氧泵气管钓鱼水族用\" href=\"//item.taobao.com/item.htm?id=536484810339\" target=\"_blank\">水族硅胶气管4*6MM增氧管冲氧泵气管钓鱼水族用</a></p>\n" +
                "\t\t\t\t\t\t\t<p class=\"price\">￥<span>1.00</span></p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p class=\"sale\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t已售出<span class=\"sale-count\">51</span>件\n" +
                "\t\t\t\t\t\t\t\t                            </p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t</div>\n");
        while(matcher.find()){
            //System.out.println(matcher.group());
            for(int i=1; i<=matcher.groupCount(); i++){
                System.out.println(i+"  group:"+matcher.group(i));
            }
        }
    }

}
