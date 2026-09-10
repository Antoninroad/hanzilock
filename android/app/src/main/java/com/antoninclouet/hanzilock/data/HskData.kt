package com.antoninclouet.hanzilock.data

import android.content.Context
import org.json.JSONArray

/**
 * Vocabulaire HSK.
 *
 * Deux sources possibles, dans l'ordre :
 *  1. `assets/hsk_full.json` — HSK 1-6 complet (~5000 mots), généré par
 *     `android/tools/build_hsk.py` (pinyin + niveaux du jeu de données HSK,
 *     français repris de la liste manuelle ci-dessous ou traduit de l'anglais).
 *  2. À défaut, la liste manuelle `rawDeck` (HSK 1-3, ~480 mots) — sert aussi
 *     de source de traductions FR de qualité pour le script.
 */
object HskData {

    val allLevels = 1..6

    @Volatile
    private var loaded: List<HanziCard>? = null

    @Volatile
    var availableLevels: Set<Int> = setOf(1, 2, 3)
        private set

    /** À appeler une fois au démarrage (Application). Sans effet si déjà chargé. */
    fun ensureLoaded(context: Context) {
        if (loaded != null) return
        synchronized(this) {
            if (loaded != null) return
            val fromAssets = runCatching { readAssets(context) }.getOrNull()
            if (fromAssets != null && fromAssets.isNotEmpty()) {
                loaded = fromAssets
                availableLevels = fromAssets.map { it.hskLevel }.toSet()
            } else {
                loaded = dedupe(rawDeck)
                availableLevels = setOf(1, 2, 3)
            }
        }
    }

    private fun readAssets(context: Context): List<HanziCard> {
        val text = context.assets.open("hsk_full.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(text)
        val out = ArrayList<HanziCard>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out += HanziCard(
                hanzi = o.getString("h"),
                pinyin = o.getString("p"),
                meaningFr = o.getString("f"),
                hskLevel = o.getInt("l"),
                category = "HSK ${o.getInt("l")}",
            )
        }
        return dedupe(out)
    }

    private fun dedupe(list: List<HanziCard>): List<HanziCard> {
        val seen = HashSet<String>()
        return list.filter { seen.add(it.hanzi) }
    }

    private fun cards(category: String, entries: List<Triple<String, String, String>>): List<HanziCard> =
        cards(category, 1, entries)

    private fun cards(category: String, level: Int, entries: List<Triple<String, String, String>>): List<HanziCard> =
        entries.map { HanziCard(it.first, it.second, it.third, hskLevel = level, category = category) }

    /** Deck complet dédupliqué. `ensureLoaded` doit avoir été appelé. */
    val deck: List<HanziCard>
        get() = loaded ?: dedupe(rawDeck)

    fun deckForLevels(levels: Set<Int>): List<HanziCard> =
        deck.filter { it.hskLevel in levels }

    private val rawDeck: List<HanziCard> = buildList {
        addAll(cards("Pronoms", listOf(
            Triple("我", "wǒ", "je / moi"), Triple("你", "nǐ", "tu / toi"), Triple("他", "tā", "il"),
            Triple("她", "tā", "elle"), Triple("我们", "wǒmen", "nous"), Triple("这", "zhè", "ceci"),
            Triple("那", "nà", "cela"), Triple("哪", "nǎ", "lequel"), Triple("哪儿", "nǎr", "où"),
            Triple("谁", "shéi", "qui"), Triple("什么", "shénme", "quoi"), Triple("几", "jǐ", "combien (petit nombre)"),
            Triple("多少", "duōshao", "combien"), Triple("怎么", "zěnme", "comment"), Triple("怎么样", "zěnmeyàng", "comment (avis)"),
        )))
        addAll(cards("Verbes", listOf(
            Triple("是", "shì", "être"), Triple("有", "yǒu", "avoir"), Triple("在", "zài", "être à / se trouver"),
            Triple("去", "qù", "aller"), Triple("来", "lái", "venir"), Triple("回", "huí", "rentrer"),
            Triple("叫", "jiào", "s'appeler"), Triple("爱", "ài", "aimer (fort)"), Triple("喜欢", "xǐhuan", "aimer"),
            Triple("想", "xiǎng", "vouloir / penser"), Triple("认识", "rènshi", "connaître"), Triple("看", "kàn", "regarder"),
            Triple("看见", "kànjiàn", "voir"), Triple("听", "tīng", "écouter"), Triple("说", "shuō", "dire / parler"),
            Triple("读", "dú", "lire à voix haute"), Triple("写", "xiě", "écrire"), Triple("做", "zuò", "faire"),
            Triple("坐", "zuò", "s'asseoir"), Triple("吃", "chī", "manger"), Triple("喝", "hē", "boire"),
            Triple("买", "mǎi", "acheter"), Triple("打电话", "dǎ diànhuà", "téléphoner"), Triple("开", "kāi", "ouvrir / conduire"),
            Triple("睡觉", "shuìjiào", "dormir"), Triple("学习", "xuéxí", "étudier"), Triple("工作", "gōngzuò", "travailler"),
            Triple("住", "zhù", "habiter"), Triple("会", "huì", "savoir (compétence)"), Triple("能", "néng", "pouvoir"),
            Triple("请", "qǐng", "prier / inviter"), Triple("下雨", "xiàyǔ", "pleuvoir"),
        )))
        addAll(cards("Adjectifs & adverbes", listOf(
            Triple("好", "hǎo", "bien"), Triple("大", "dà", "grand"), Triple("小", "xiǎo", "petit"),
            Triple("多", "duō", "beaucoup"), Triple("少", "shǎo", "peu"), Triple("冷", "lěng", "froid"),
            Triple("热", "rè", "chaud"), Triple("高兴", "gāoxìng", "content"), Triple("漂亮", "piàoliang", "joli"),
            Triple("很", "hěn", "très"), Triple("太", "tài", "trop"), Triple("都", "dōu", "tous"),
            Triple("也", "yě", "aussi"), Triple("不", "bù", "ne...pas"), Triple("没有", "méiyǒu", "ne pas avoir"),
        )))
        addAll(cards("Famille & personnes", listOf(
            Triple("爸爸", "bàba", "papa"), Triple("妈妈", "māma", "maman"), Triple("儿子", "érzi", "fils"),
            Triple("女儿", "nǚ'ér", "fille"), Triple("朋友", "péngyou", "ami"), Triple("老师", "lǎoshī", "professeur"),
            Triple("学生", "xuésheng", "étudiant"), Triple("同学", "tóngxué", "camarade"), Triple("先生", "xiānsheng", "monsieur"),
            Triple("小姐", "xiǎojiě", "mademoiselle"), Triple("医生", "yīshēng", "médecin"), Triple("人", "rén", "personne"),
        )))
        addAll(cards("Nombres & temps", listOf(
            Triple("一", "yī", "un"), Triple("二", "èr", "deux"), Triple("三", "sān", "trois"), Triple("四", "sì", "quatre"),
            Triple("五", "wǔ", "cinq"), Triple("六", "liù", "six"), Triple("七", "qī", "sept"), Triple("八", "bā", "huit"),
            Triple("九", "jiǔ", "neuf"), Triple("十", "shí", "dix"), Triple("岁", "suì", "âge (ans)"),
            Triple("年", "nián", "année"), Triple("月", "yuè", "mois"), Triple("号", "hào", "jour du mois"),
            Triple("星期", "xīngqī", "semaine"), Triple("今天", "jīntiān", "aujourd'hui"), Triple("明天", "míngtiān", "demain"),
            Triple("昨天", "zuótiān", "hier"), Triple("上午", "shàngwǔ", "matin"), Triple("中午", "zhōngwǔ", "midi"),
            Triple("下午", "xiàwǔ", "après-midi"), Triple("点", "diǎn", "heure"), Triple("分钟", "fēnzhōng", "minute"),
            Triple("现在", "xiànzài", "maintenant"), Triple("时候", "shíhou", "moment"), Triple("一点儿", "yìdiǎnr", "un peu"),
        )))
        addAll(cards("Nourriture & objets", listOf(
            Triple("米饭", "mǐfàn", "riz"), Triple("菜", "cài", "plat"), Triple("水", "shuǐ", "eau"),
            Triple("茶", "chá", "thé"), Triple("水果", "shuǐguǒ", "fruit"), Triple("苹果", "píngguǒ", "pomme"),
            Triple("杯子", "bēizi", "verre"), Triple("东西", "dōngxi", "chose"), Triple("书", "shū", "livre"),
            Triple("桌子", "zhuōzi", "table"), Triple("椅子", "yǐzi", "chaise"), Triple("电脑", "diànnǎo", "ordinateur"),
            Triple("电视", "diànshì", "télévision"), Triple("电影", "diànyǐng", "film"), Triple("衣服", "yīfu", "vêtements"),
            Triple("字", "zì", "caractère / écriture"), Triple("猫", "māo", "chat"), Triple("狗", "gǒu", "chien"),
        )))
        addAll(cards("Lieux & déplacement", listOf(
            Triple("中国", "Zhōngguó", "Chine"), Triple("北京", "Běijīng", "Pékin"), Triple("家", "jiā", "maison / famille"),
            Triple("学校", "xuéxiào", "école"), Triple("商店", "shāngdiàn", "magasin"), Triple("饭店", "fàndiàn", "restaurant / hôtel"),
            Triple("医院", "yīyuàn", "hôpital"), Triple("里", "lǐ", "dans"), Triple("上", "shàng", "sur"),
            Triple("下", "xià", "sous"), Triple("前面", "qiánmiàn", "devant"), Triple("后面", "hòumiàn", "derrière"),
            Triple("出租车", "chūzūchē", "taxi"), Triple("飞机", "fēijī", "avion"),
        )))
        addAll(cards("Expressions", listOf(
            Triple("你好", "nǐ hǎo", "bonjour"), Triple("谢谢", "xièxie", "merci"), Triple("不客气", "bú kèqi", "de rien"),
            Triple("对不起", "duìbuqǐ", "désolé"), Triple("没关系", "méi guānxi", "ce n'est rien"), Triple("再见", "zàijiàn", "au revoir"),
            Triple("喂", "wèi", "allô"), Triple("和", "hé", "et"), Triple("的", "de", "particule possessive"),
            Triple("了", "le", "particule accompli"), Triple("吗", "ma", "particule question"), Triple("呢", "ne", "particule question"),
            Triple("个", "gè", "mot de mesure générique"), Triple("些", "xiē", "quelques"), Triple("天气", "tiānqì", "météo"),
            Triple("名字", "míngzi", "nom"),
        )))

        // ---------- HSK 2 (≈150 nouveaux mots) ----------
        addAll(cards("HSK2 · Personnes & famille", 2, listOf(
            Triple("哥哥", "gēge", "grand frère"), Triple("姐姐", "jiějie", "grande sœur"),
            Triple("弟弟", "dìdi", "petit frère"), Triple("妹妹", "mèimei", "petite sœur"),
            Triple("孩子", "háizi", "enfant"), Triple("丈夫", "zhàngfu", "mari"),
            Triple("妻子", "qīzi", "épouse"), Triple("男人", "nánrén", "homme"),
            Triple("女人", "nǚrén", "femme"), Triple("大家", "dàjiā", "tout le monde"),
            Triple("您", "nín", "vous (poli)"), Triple("它", "tā", "il / elle (objet, animal)"),
            Triple("服务员", "fúwùyuán", "serveur / serveuse"),
        )))
        addAll(cards("HSK2 · Verbes", 2, listOf(
            Triple("帮助", "bāngzhù", "aider"), Triple("唱歌", "chànggē", "chanter"),
            Triple("出", "chū", "sortir"), Triple("穿", "chuān", "porter (un vêtement)"),
            Triple("等", "děng", "attendre"), Triple("懂", "dǒng", "comprendre"),
            Triple("告诉", "gàosu", "dire à / informer"), Triple("给", "gěi", "donner"),
            Triple("觉得", "juéde", "trouver / avoir l'impression"), Triple("介绍", "jièshào", "présenter"),
            Triple("进", "jìn", "entrer"), Triple("开始", "kāishǐ", "commencer"),
            Triple("卖", "mài", "vendre"), Triple("让", "ràng", "laisser / faire faire"),
            Triple("起床", "qǐchuáng", "se lever (du lit)"), Triple("上班", "shàngbān", "aller au travail"),
            Triple("生病", "shēngbìng", "tomber malade"), Triple("说话", "shuōhuà", "parler"),
            Triple("送", "sòng", "offrir / raccompagner"), Triple("跳舞", "tiàowǔ", "danser"),
            Triple("完", "wán", "finir"), Triple("玩", "wán", "jouer / s'amuser"),
            Triple("问", "wèn", "demander"), Triple("洗", "xǐ", "laver"),
            Triple("希望", "xīwàng", "espérer"), Triple("笑", "xiào", "rire / sourire"),
            Triple("姓", "xìng", "s'appeler (nom de famille)"), Triple("休息", "xiūxi", "se reposer"),
            Triple("游泳", "yóuyǒng", "nager"), Triple("运动", "yùndòng", "faire du sport"),
            Triple("找", "zhǎo", "chercher"), Triple("准备", "zhǔnbèi", "préparer"),
            Triple("走", "zǒu", "marcher / partir"), Triple("知道", "zhīdào", "savoir"),
            Triple("踢足球", "tī zúqiú", "jouer au football"), Triple("打篮球", "dǎ lánqiú", "jouer au basket"),
            Triple("跑步", "pǎobù", "courir"), Triple("旅游", "lǚyóu", "voyager"),
        )))
        addAll(cards("HSK2 · Adjectifs & adverbes", 2, listOf(
            Triple("白", "bái", "blanc"), Triple("长", "cháng", "long"),
            Triple("错", "cuò", "faux / erroné"), Triple("非常", "fēicháng", "extrêmement"),
            Triple("高", "gāo", "haut / grand (taille)"), Triple("好吃", "hǎochī", "bon (au goût)"),
            Triple("黑", "hēi", "noir"), Triple("红", "hóng", "rouge"),
            Triple("近", "jìn", "proche"), Triple("快", "kuài", "rapide"),
            Triple("快乐", "kuàilè", "joyeux"), Triple("累", "lèi", "fatigué"),
            Triple("慢", "màn", "lent"), Triple("忙", "máng", "occupé"),
            Triple("便宜", "piányi", "bon marché"), Triple("晴", "qíng", "ensoleillé"),
            Triple("阴", "yīn", "couvert (temps)"), Triple("新", "xīn", "nouveau"),
            Triple("真", "zhēn", "vraiment"), Triple("贵", "guì", "cher"),
            Triple("最", "zuì", "le plus"), Triple("还", "hái", "encore / en plus"),
            Triple("一起", "yìqǐ", "ensemble"), Triple("已经", "yǐjīng", "déjà"),
            Triple("再", "zài", "de nouveau"), Triple("正在", "zhèngzài", "en train de"),
        )))
        addAll(cards("HSK2 · Temps", 2, listOf(
            Triple("早上", "zǎoshang", "le matin"), Triple("晚上", "wǎnshang", "le soir"),
            Triple("小时", "xiǎoshí", "heure (durée)"), Triple("分", "fēn", "minute"),
            Triple("时间", "shíjiān", "temps / moment"), Triple("去年", "qùnián", "l'année dernière"),
            Triple("每", "měi", "chaque"), Triple("生日", "shēngrì", "anniversaire"),
            Triple("日", "rì", "jour"), Triple("第一", "dìyī", "premier"),
        )))
        addAll(cards("HSK2 · Lieux & transports", 2, listOf(
            Triple("宾馆", "bīnguǎn", "hôtel"), Triple("房间", "fángjiān", "chambre / pièce"),
            Triple("公司", "gōngsī", "entreprise"), Triple("教室", "jiàoshì", "salle de classe"),
            Triple("机场", "jīchǎng", "aéroport"), Triple("火车站", "huǒchēzhàn", "gare"),
            Triple("公共汽车", "gōnggòng qìchē", "bus"), Triple("自行车", "zìxíngchē", "vélo"),
            Triple("路", "lù", "route / rue"), Triple("门", "mén", "porte"),
            Triple("旁边", "pángbiān", "à côté"), Triple("外", "wài", "dehors / extérieur"),
            Triple("左边", "zuǒbian", "à gauche"), Triple("右边", "yòubian", "à droite"),
            Triple("离", "lí", "à (distance de)"), Triple("从", "cóng", "depuis / à partir de"),
            Triple("往", "wǎng", "vers"), Triple("到", "dào", "arriver / jusqu'à"),
            Triple("远", "yuǎn", "loin"),
        )))
        addAll(cards("HSK2 · Objets & nourriture", 2, listOf(
            Triple("报纸", "bàozhǐ", "journal"), Triple("咖啡", "kāfēi", "café"),
            Triple("鸡蛋", "jīdàn", "œuf"), Triple("面条", "miàntiáo", "nouilles"),
            Triple("牛奶", "niúnǎi", "lait"), Triple("西瓜", "xīguā", "pastèque"),
            Triple("羊肉", "yángròu", "viande de mouton"), Triple("鱼", "yú", "poisson"),
            Triple("手表", "shǒubiǎo", "montre"), Triple("手机", "shǒujī", "téléphone portable"),
            Triple("铅笔", "qiānbǐ", "crayon"), Triple("票", "piào", "billet / ticket"),
            Triple("药", "yào", "médicament"), Triple("颜色", "yánsè", "couleur"),
            Triple("眼睛", "yǎnjing", "œil"), Triple("身体", "shēntǐ", "corps / santé"),
            Triple("雪", "xuě", "neige"),
        )))
        addAll(cards("HSK2 · Notions & grammaire", 2, listOf(
            Triple("百", "bǎi", "cent"), Triple("千", "qiān", "mille"),
            Triple("零", "líng", "zéro"), Triple("两", "liǎng", "deux (avec classificateur)"),
            Triple("次", "cì", "fois (occurrence)"), Triple("件", "jiàn", "classificateur (vêtements, affaires)"),
            Triple("比", "bǐ", "comparé à / que"), Triple("别", "bié", "ne pas (impératif)"),
            Triple("吧", "ba", "particule (suggestion)"), Triple("得", "de", "particule (complément de degré)"),
            Triple("着", "zhe", "particule (état en cours)"), Triple("过", "guò", "particule (expérience vécue)"),
            Triple("就", "jiù", "alors / précisément"), Triple("可能", "kěnéng", "peut-être / possible"),
            Triple("可以", "kěyǐ", "pouvoir / être permis"), Triple("要", "yào", "vouloir / devoir"),
            Triple("为什么", "wèishénme", "pourquoi"), Triple("因为", "yīnwèi", "parce que"),
            Triple("所以", "suǒyǐ", "donc / c'est pourquoi"), Triple("虽然", "suīrán", "bien que"),
            Triple("但是", "dànshì", "mais"), Triple("意思", "yìsi", "sens / signification"),
            Triple("问题", "wèntí", "problème / question"), Triple("事情", "shìqing", "affaire / chose"),
            Triple("题", "tí", "question / exercice"), Triple("课", "kè", "cours / leçon"),
            Triple("考试", "kǎoshì", "examen"), Triple("一下", "yíxià", "un instant / un peu"),
            Triple("对", "duì", "correct / envers"),
        )))

        // ---------- HSK 3 · lot 1 (≈160 mots) ----------
        addAll(cards("HSK3 · Verbes", 3, listOf(
            Triple("搬", "bān", "déménager / transporter"), Triple("帮忙", "bāngmáng", "donner un coup de main"),
            Triple("表示", "biǎoshì", "exprimer / manifester"), Triple("参加", "cānjiā", "participer à"),
            Triple("迟到", "chídào", "arriver en retard"), Triple("打扫", "dǎsǎo", "faire le ménage"),
            Triple("打算", "dǎsuàn", "avoir l'intention de"), Triple("担心", "dānxīn", "s'inquiéter"),
            Triple("锻炼", "duànliàn", "s'entraîner (sport)"), Triple("放", "fàng", "poser / laisser"),
            Triple("复习", "fùxí", "réviser"), Triple("感冒", "gǎnmào", "s'enrhumer"),
            Triple("关", "guān", "fermer / éteindre"), Triple("关心", "guānxīn", "se soucier de"),
            Triple("害怕", "hàipà", "avoir peur"), Triple("花", "huā", "dépenser (argent, temps)"),
            Triple("换", "huàn", "échanger / changer"), Triple("回答", "huídá", "répondre"),
            Triple("检查", "jiǎnchá", "vérifier / examiner"), Triple("见面", "jiànmiàn", "se rencontrer"),
            Triple("讲", "jiǎng", "raconter / expliquer"), Triple("教", "jiāo", "enseigner"),
            Triple("结婚", "jiéhūn", "se marier"), Triple("结束", "jiéshù", "se terminer"),
            Triple("借", "jiè", "emprunter / prêter"), Triple("决定", "juédìng", "décider"),
            Triple("哭", "kū", "pleurer"), Triple("离开", "líkāi", "quitter / partir"),
            Triple("练习", "liànxí", "s'exercer"), Triple("了解", "liǎojiě", "bien connaître"),
            Triple("留学", "liúxué", "étudier à l'étranger"), Triple("拿", "ná", "prendre / tenir"),
            Triple("骑", "qí", "monter (vélo, cheval)"), Triple("认为", "rènwéi", "estimer / penser que"),
            Triple("生气", "shēngqì", "se fâcher"), Triple("提高", "tígāo", "améliorer"),
            Triple("同意", "tóngyì", "être d'accord"), Triple("完成", "wánchéng", "achever"),
            Triple("忘记", "wàngjì", "oublier"), Triple("洗澡", "xǐzǎo", "se doucher"),
            Triple("相信", "xiāngxìn", "croire / faire confiance"), Triple("小心", "xiǎoxīn", "faire attention"),
            Triple("需要", "xūyào", "avoir besoin de"), Triple("选择", "xuǎnzé", "choisir"),
            Triple("影响", "yǐngxiǎng", "influencer / influence"), Triple("用", "yòng", "utiliser"),
            Triple("遇到", "yùdào", "rencontrer par hasard"), Triple("愿意", "yuànyì", "être disposé à"),
            Triple("站", "zhàn", "se tenir debout"), Triple("照顾", "zhàogù", "s'occuper de"),
            Triple("注意", "zhùyì", "faire attention à"), Triple("出现", "chūxiàn", "apparaître"),
            Triple("带", "dài", "apporter / emporter"), Triple("必须", "bìxū", "devoir absolument"),
        )))
        addAll(cards("HSK3 · Gens", 3, listOf(
            Triple("邻居", "línjū", "voisin"), Triple("奶奶", "nǎinai", "grand-mère (paternelle)"),
            Triple("爷爷", "yéye", "grand-père (paternel)"), Triple("阿姨", "āyí", "tante / madame"),
            Triple("叔叔", "shūshu", "oncle"), Triple("经理", "jīnglǐ", "directeur / manager"),
            Triple("护士", "hùshi", "infirmier / infirmière"), Triple("警察", "jǐngchá", "policier"),
            Triple("司机", "sījī", "chauffeur"), Triple("同事", "tóngshì", "collègue"),
            Triple("客人", "kèrén", "invité / client"), Triple("校长", "xiàozhǎng", "directeur d'école"),
            Triple("个子", "gèzi", "taille (stature)"), Triple("声音", "shēngyīn", "son / voix"),
        )))
        addAll(cards("HSK3 · Vie quotidienne", 3, listOf(
            Triple("办法", "bànfǎ", "méthode / solution"), Triple("成绩", "chéngjì", "résultats / notes"),
            Triple("词典", "cídiǎn", "dictionnaire"), Triple("电梯", "diàntī", "ascenseur"),
            Triple("地方", "dìfang", "endroit"), Triple("地铁", "dìtiě", "métro"),
            Triple("附近", "fùjìn", "les environs"), Triple("故事", "gùshi", "histoire (récit)"),
            Triple("黑板", "hēibǎn", "tableau (noir)"), Triple("会议", "huìyì", "réunion"),
            Triple("机会", "jīhuì", "occasion"), Triple("季节", "jìjié", "saison"),
            Triple("街道", "jiēdào", "rue / avenue"), Triple("节目", "jiémù", "programme / émission"),
            Triple("节日", "jiérì", "fête / jour férié"), Triple("句子", "jùzi", "phrase"),
            Triple("筷子", "kuàizi", "baguettes"), Triple("历史", "lìshǐ", "histoire (matière)"),
            Triple("礼物", "lǐwù", "cadeau"), Triple("脸", "liǎn", "visage"),
            Triple("裤子", "kùzi", "pantalon"), Triple("帽子", "màozi", "chapeau"),
            Triple("面包", "miànbāo", "pain"), Triple("啤酒", "píjiǔ", "bière"),
            Triple("葡萄", "pútao", "raisin"), Triple("盘子", "pánzi", "assiette"),
            Triple("裙子", "qúnzi", "jupe"), Triple("生活", "shēnghuó", "vie"),
            Triple("世界", "shìjiè", "monde"), Triple("数学", "shùxué", "mathématiques"),
            Triple("水平", "shuǐpíng", "niveau"), Triple("太阳", "tàiyáng", "soleil"),
            Triple("腿", "tuǐ", "jambe"), Triple("碗", "wǎn", "bol"),
            Triple("文化", "wénhuà", "culture"), Triple("鞋", "xié", "chaussure"),
            Triple("香蕉", "xiāngjiāo", "banane"), Triple("音乐", "yīnyuè", "musique"),
            Triple("银行", "yínháng", "banque"), Triple("饮料", "yǐnliào", "boisson"),
            Triple("邮局", "yóujú", "bureau de poste"), Triple("月亮", "yuèliang", "lune"),
            Triple("中间", "zhōngjiān", "le milieu"), Triple("周末", "zhōumò", "week-end"),
            Triple("嘴", "zuǐ", "bouche"), Triple("作业", "zuòyè", "devoirs (scolaires)"),
            Triple("环境", "huánjìng", "environnement"), Triple("花园", "huāyuán", "jardin"),
            Triple("习惯", "xíguàn", "habitude"), Triple("刀", "dāo", "couteau"),
        )))
        addAll(cards("HSK3 · Adjectifs & adverbes", 3, listOf(
            Triple("安静", "ānjìng", "calme / silencieux"), Triple("矮", "ǎi", "petit (de taille)"),
            Triple("饱", "bǎo", "rassasié"), Triple("差", "chà", "médiocre / manquer"),
            Triple("聪明", "cōngmíng", "intelligent"), Triple("干净", "gānjìng", "propre"),
            Triple("更", "gèng", "encore plus"), Triple("坏", "huài", "mauvais / cassé"),
            Triple("简单", "jiǎndān", "simple"), Triple("渴", "kě", "assoiffé"),
            Triple("可爱", "kě'ài", "mignon"), Triple("苦", "kǔ", "amer"),
            Triple("老", "lǎo", "vieux"), Triple("绿", "lǜ", "vert"),
            Triple("蓝", "lán", "bleu"), Triple("胖", "pàng", "gros / gras"),
            Triple("奇怪", "qíguài", "étrange"), Triple("清楚", "qīngchu", "clair / net"),
            Triple("认真", "rènzhēn", "sérieux / appliqué"), Triple("瘦", "shòu", "mince / maigre"),
            Triple("舒服", "shūfu", "à l'aise / confortable"), Triple("特别", "tèbié", "spécial / surtout"),
            Triple("甜", "tián", "sucré"), Triple("突然", "tūrán", "soudain"),
            Triple("危险", "wēixiǎn", "dangereux"), Triple("新鲜", "xīnxiān", "frais"),
            Triple("一样", "yíyàng", "identique / pareil"), Triple("有名", "yǒumíng", "célèbre"),
            Triple("重要", "zhòngyào", "important"), Triple("努力", "nǔlì", "assidu / faire des efforts"),
            Triple("热情", "rèqíng", "chaleureux"), Triple("容易", "róngyì", "facile"),
            Triple("经常", "jīngcháng", "souvent"), Triple("一般", "yìbān", "ordinaire / en général"),
            Triple("一直", "yìzhí", "sans cesse / tout droit"), Triple("终于", "zhōngyú", "finalement"),
            Triple("总是", "zǒngshì", "toujours"), Triple("又", "yòu", "de nouveau / en plus"),
        )))
        addAll(cards("HSK3 · Temps & connecteurs", 3, listOf(
            Triple("以前", "yǐqián", "avant / autrefois"), Triple("以后", "yǐhòu", "après / plus tard"),
            Triple("最近", "zuìjìn", "récemment"), Triple("然后", "ránhòu", "ensuite"),
            Triple("刚才", "gāngcái", "à l'instant"), Triple("一会儿", "yíhuìr", "un moment"),
            Triple("其实", "qíshí", "en fait"), Triple("其他", "qítā", "les autres / autre"),
            Triple("比如", "bǐrú", "par exemple"), Triple("除了", "chúle", "sauf / à part"),
            Triple("而且", "érqiě", "de plus / et en outre"), Triple("或者", "huòzhě", "ou bien"),
            Triple("还是", "háishì", "ou (dans une question)"), Triple("可是", "kěshì", "mais"),
            Triple("如果", "rúguǒ", "si"), Triple("根据", "gēnjù", "selon / d'après"),
            Triple("关于", "guānyú", "à propos de"), Triple("只", "zhǐ", "seulement"),
            Triple("被", "bèi", "particule du passif"), Triple("把", "bǎ", "particule d'objet antéposé"),
            Triple("为了", "wèile", "afin de"), Triple("自己", "zìjǐ", "soi-même"),
            Triple("别人", "biéren", "les autres / autrui"), Triple("空调", "kōngtiáo", "climatiseur"),
        )))
    }
}
