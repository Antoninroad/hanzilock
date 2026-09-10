import Foundation

/// Liste HSK1 (~150 mots), regroupée par thème pour lisibilité.
/// Reprend le contenu du système de révision "Chinois_Notion_Systeme.md".
enum HSKData {
    private static func cards(_ category: String, _ entries: [(String, String, String)]) -> [HanziCard] {
        entries.map { HanziCard(hanzi: $0.0, pinyin: $0.1, meaningFR: $0.2, hskLevel: 1, category: category) }
    }

    static let deck: [HanziCard] = {
        var all: [HanziCard] = []

        all += cards("Pronoms", [
            ("我", "wǒ", "je / moi"), ("你", "nǐ", "tu / toi"), ("他", "tā", "il"),
            ("她", "tā", "elle"), ("我们", "wǒmen", "nous"), ("这", "zhè", "ceci"),
            ("那", "nà", "cela"), ("哪", "nǎ", "lequel"), ("哪儿", "nǎr", "où"),
            ("谁", "shéi", "qui"), ("什么", "shénme", "quoi"), ("几", "jǐ", "combien (petit nombre)"),
            ("多少", "duōshao", "combien"), ("怎么", "zěnme", "comment"), ("怎么样", "zěnmeyàng", "comment (avis)")
        ])

        all += cards("Verbes", [
            ("是", "shì", "être"), ("有", "yǒu", "avoir"), ("在", "zài", "être à / se trouver"),
            ("去", "qù", "aller"), ("来", "lái", "venir"), ("回", "huí", "rentrer"),
            ("叫", "jiào", "s'appeler"), ("爱", "ài", "aimer (fort)"), ("喜欢", "xǐhuan", "aimer"),
            ("想", "xiǎng", "vouloir / penser"), ("认识", "rènshi", "connaître"), ("看", "kàn", "regarder"),
            ("看见", "kànjiàn", "voir"), ("听", "tīng", "écouter"), ("说", "shuō", "dire / parler"),
            ("读", "dú", "lire à voix haute"), ("写", "xiě", "écrire"), ("做", "zuò", "faire"),
            ("坐", "zuò", "s'asseoir"), ("吃", "chī", "manger"), ("喝", "hē", "boire"),
            ("买", "mǎi", "acheter"), ("打电话", "dǎ diànhuà", "téléphoner"), ("开", "kāi", "ouvrir / conduire"),
            ("睡觉", "shuìjiào", "dormir"), ("学习", "xuéxí", "étudier"), ("工作", "gōngzuò", "travailler"),
            ("住", "zhù", "habiter"), ("会", "huì", "savoir (compétence)"), ("能", "néng", "pouvoir"),
            ("请", "qǐng", "prier / inviter"), ("下雨", "xiàyǔ", "pleuvoir")
        ])

        all += cards("Adjectifs & adverbes", [
            ("好", "hǎo", "bien"), ("大", "dà", "grand"), ("小", "xiǎo", "petit"),
            ("多", "duō", "beaucoup"), ("少", "shǎo", "peu"), ("冷", "lěng", "froid"),
            ("热", "rè", "chaud"), ("高兴", "gāoxìng", "content"), ("漂亮", "piàoliang", "joli"),
            ("很", "hěn", "très"), ("太", "tài", "trop"), ("都", "dōu", "tous"),
            ("也", "yě", "aussi"), ("不", "bù", "ne...pas"), ("没有", "méiyǒu", "ne pas avoir")
        ])

        all += cards("Famille & personnes", [
            ("爸爸", "bàba", "papa"), ("妈妈", "māma", "maman"), ("儿子", "érzi", "fils"),
            ("女儿", "nǚ'ér", "fille"), ("朋友", "péngyou", "ami"), ("老师", "lǎoshī", "professeur"),
            ("学生", "xuésheng", "étudiant"), ("同学", "tóngxué", "camarade"), ("先生", "xiānsheng", "monsieur"),
            ("小姐", "xiǎojiě", "mademoiselle"), ("医生", "yīshēng", "médecin"), ("人", "rén", "personne")
        ])

        all += cards("Nombres & temps", [
            ("一", "yī", "un"), ("二", "èr", "deux"), ("三", "sān", "trois"), ("四", "sì", "quatre"),
            ("五", "wǔ", "cinq"), ("六", "liù", "six"), ("七", "qī", "sept"), ("八", "bā", "huit"),
            ("九", "jiǔ", "neuf"), ("十", "shí", "dix"), ("岁", "suì", "âge (ans)"),
            ("年", "nián", "année"), ("月", "yuè", "mois"), ("号", "hào", "jour du mois"),
            ("星期", "xīngqī", "semaine"), ("今天", "jīntiān", "aujourd'hui"), ("明天", "míngtiān", "demain"),
            ("昨天", "zuótiān", "hier"), ("上午", "shàngwǔ", "matin"), ("中午", "zhōngwǔ", "midi"),
            ("下午", "xiàwǔ", "après-midi"), ("点", "diǎn", "heure"), ("分钟", "fēnzhōng", "minute"),
            ("现在", "xiànzài", "maintenant"), ("时候", "shíhou", "moment"), ("一点儿", "yìdiǎnr", "un peu")
        ])

        all += cards("Nourriture & objets", [
            ("米饭", "mǐfàn", "riz"), ("菜", "cài", "plat"), ("水", "shuǐ", "eau"),
            ("茶", "chá", "thé"), ("水果", "shuǐguǒ", "fruit"), ("苹果", "píngguǒ", "pomme"),
            ("杯子", "bēizi", "verre"), ("东西", "dōngxi", "chose"), ("书", "shū", "livre"),
            ("桌子", "zhuōzi", "table"), ("椅子", "yǐzi", "chaise"), ("电脑", "diànnǎo", "ordinateur"),
            ("电视", "diànshì", "télévision"), ("电影", "diànyǐng", "film"), ("衣服", "yīfu", "vêtements"),
            ("字", "zì", "caractère / écriture"), ("猫", "māo", "chat"), ("狗", "gǒu", "chien")
        ])

        all += cards("Lieux & déplacement", [
            ("中国", "Zhōngguó", "Chine"), ("北京", "Běijīng", "Pékin"), ("家", "jiā", "maison / famille"),
            ("学校", "xuéxiào", "école"), ("商店", "shāngdiàn", "magasin"), ("饭店", "fàndiàn", "restaurant / hôtel"),
            ("医院", "yīyuàn", "hôpital"), ("里", "lǐ", "dans"), ("上", "shàng", "sur"),
            ("下", "xià", "sous"), ("前面", "qiánmiàn", "devant"), ("后面", "hòumiàn", "derrière"),
            ("出租车", "chūzūchē", "taxi"), ("飞机", "fēijī", "avion")
        ])

        all += cards("Expressions", [
            ("你好", "nǐ hǎo", "bonjour"), ("谢谢", "xièxie", "merci"), ("不客气", "bú kèqi", "de rien"),
            ("对不起", "duìbuqǐ", "désolé"), ("没关系", "méi guānxi", "ce n'est rien"), ("再见", "zàijiàn", "au revoir"),
            ("喂", "wèi", "allô"), ("和", "hé", "et"), ("的", "de", "particule possessive"),
            ("了", "le", "particule accompli"), ("吗", "ma", "particule question"), ("呢", "ne", "particule question"),
            ("个", "gè", "mot de mesure générique"), ("些", "xiē", "quelques"), ("天气", "tiānqì", "météo"),
            ("名字", "míngzi", "nom")
        ])

        return all
    }()
}
