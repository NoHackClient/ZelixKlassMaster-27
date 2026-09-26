package com.zelix.klassmaster.config;

import com.zelix.UserPreferences;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ZkmUtils;

public abstract class HiddenOptionFlags extends SystemEnvironmentConstants {
    public static final String PATH_SEPARATOR = System.getProperty("path.separator", ";");
    public static final UserPreferences USER_PREFERENCES = new UserPreferences(SystemEnvironmentConstants.USER_DIR);
    public static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
    public static final String CANARY_PROPERTY_01 = ZkmUtils.getHashedSystemProperty(
            "48666e3f7568e2bd79d2764bb93af45158d05a5b1f11c91c2ee8f1c0171db67298db84c9c56a7fcfc60a8708b264858cfadcb573fe55fe3704b212486d575455"
    );
    public static final boolean CANARY_PROPERTY_02 = ZkmUtils.getHashedSystemProperty(
                    "f0787e446f27d2e2b44b0cb035a67fd5f64caaccc324fb3a16e4f6cd68da091c4c5725ab6cea73c99a669742ab8f2a4e43a850702830efb2557357af1e98a226", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_03 = !ZkmUtils.getHashedSystemProperty(
                    "c7ee9e8761394cad055659e502a2fe4a0921a6ed0ba5730f694eb61042e2d23221fb5dee687aa1fc4b01c52ceb88d605e24fbff91e2ea630c766c633a7e59fc1", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_04 = ZkmUtils.getHashedSystemProperty(
            "943609f07da1a9b940d2f26fafb769ac1a12c58f0b224e0252738b3b434c191b01a3ed5c638de2a93a397fd2d865ef19422d02f7055c9fa07b6d006770a1ff73"
    );
    public static final boolean CANARY_PROPERTY_05 = ZkmUtils.getHashedSystemProperty(
                    "294a69f697a99b4c01f5d5a43b184a70a72b047028894ac32ba67f741cbdefe99512bf3cecdc0cb8257d69d67f19d6ecd84d11d4ac6ffd59d6af70be79f62495", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_06 = !ZkmUtils.getHashedSystemProperty(
                    "b784de1bebfca4bcea3f65561b520dbc9bd499ac6198b610bbb179647dc95a5f2d98bcebee8b3ccd9c7b16da15446379cdce9c9694c27401219b5b96a7f24656", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_07 = ZkmUtils.getHashedSystemProperty(
            "3842d39430eb1a5976c4895f62a41b15b0d4dc590aadf6259086e5056e560cad9f529fcbbc41787a64ae54c3dca061b7a5ee3ba371ac00cd0ffee65c39731662"
    );
    public static final boolean CANARY_PROPERTY_08 = ZkmUtils.getHashedSystemProperty(
                    "87eff0ab2e84114b5756a7c03bed43a29a0db2dfd076f07526fcae73423dbf496f7e2782fa1c79ee9868d0d15a19b04bfd20129d1812d40047e862a08810427c", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_09 = !ZkmUtils.getHashedSystemProperty(
                    "5f9521ca702c947329c12ef9b2410849b8579a90c18f5ba528523773dd1e4612f0755e0082c03cf70b3191a627e568de8958523557c91375cc43e16caf60133b", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_10 = ZkmUtils.getHashedSystemProperty(
            "975cbf9a202deba39cb8689e83f33ef059a3169e0f2428a35ac57f44129a306adabce5587fadca89664db998a3b4a71a11d7f208c9cf05b371679b249ef0b11b"
    );
    public static final boolean CANARY_PROPERTY_11 = ZkmUtils.getHashedSystemProperty(
                    "c43f452302034b835b38a32a6887fc251cabc8fa82be3730e82e8fbd7e64d93381b52e2dcee5a35fcf818d05080480cf84d1bff5a83ed4cfbcf41a68a514a5a2", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_12 = !ZkmUtils.getHashedSystemProperty(
                    "3cd8a1077b8ec60d9ac3dc4fad4dd1193580e897f41cfa5fc51b8f33613623ac71a3a13b2b3491a8999c73c88a95fb0dfcd08937b9302ff21ea444f1552e9dab", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_13 = ZkmUtils.getHashedSystemProperty(
            "fbe3c84d3a7e901274d68297a1219bf2e8fb5fae0d6bd872165f5566127ff9870594c0d339e17a4d3399205f51df6a9aa1a0a6cb672c60b91d0e15131f8d2974"
    );
    public static final boolean CANARY_PROPERTY_14 = ZkmUtils.getHashedSystemProperty(
                    "9e4f1da0a2d2821e3491b76c61ce07067e17555e0a7925fef55c205b07dd0abaec46989998abd34321ac6a17688ba1a11058e4bf4dc7872eb9a349630bbea150", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_15 = !ZkmUtils.getHashedSystemProperty(
                    "4bdba9bd2d6ae55b9e697eb44a8a2f166e235f93842277b1a179cf6a0398a34346ffc38c4d74ab68512ba29aa82eff44920bed9a3f95658a7b6dec1b88031d57", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_16 = ZkmUtils.getHashedSystemProperty(
            "043e5abda9d85df382f390389046786bc5a25a2d45a17f4d90d428e6a76c620f43730b600f810a2af71e8d5b658c079953a04001563dfb10019f578bc97ee8c0"
    );
    public static final boolean CANARY_PROPERTY_17 = ZkmUtils.getHashedSystemProperty(
                    "3725536e69fa7d1c71e38a31469222d483a61932df39ee53f4af1e5815827905f691b47ac8fe872b758bd9e8e9961830be7344b24444bec25376f8018249b93d", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_18 = !ZkmUtils.getHashedSystemProperty(
                    "9fffbcb65b2bc5a4b5d3d8536d14e70a854a121cdb50632bd40d0262aa7fe611ed48583e93ddcdae24382290e567dcc2c1818d720dd3b4fb146cf0e94efed258", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_19 = ZkmUtils.getHashedSystemProperty(
            "6c8f92b3e0ada5c7b4f8ceb1852f66a75cf98152d516393ae06a5ae0c818d953a631e8d4c2721f73fd615bb20add1bdcdc1a3be2fad7c0526f556fc1db6c17ac"
    );
    public static final boolean CANARY_PROPERTY_20 = ZkmUtils.getHashedSystemProperty(
                    "8796cdd50f9090eaafa0781a4b97b1bc11007d7922c7aa6ab370331bf23fb7eb923cc51f7e6d64181b4a0eba90eb6f751e68e9b63590791b46aee8efcbe23518", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_21 = !ZkmUtils.getHashedSystemProperty(
                    "3a40080a54785fc77aa172387fa3abe33e0fb7df865e2de7a72823ddf1f91a6b7f14325ceb53a90d499c57f4085b0cef3cb760cc435c307083984284b68197ce", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_22 = ZkmUtils.getHashedSystemProperty(
            "6d3f635b90c2ab762820a1867b842ba22aeeb5c0ff70f285e78d967e4533e808a3fa6feed6ad1a2d30ed8118341584b66ddbb6135b94dd8b3ac9e3a5eb5467fa"
    );
    public static final boolean CANARY_PROPERTY_23 = ZkmUtils.getHashedSystemProperty(
                    "33b0b6949f2764de8951ed3607c6e6ab1dd3f5767d025c31142b4fc4c27e147c3f87533290c01fbf25d853c0a89e71b526bf9f9853bfad1e737a3cf0ce1b15ab", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_24 = !ZkmUtils.getHashedSystemProperty(
                    "6407357bbdd1601c73e6e371f7a9d7306cca33d1e65388ed5b00a2913de310daaf7d278e0c896e549fdb29783a68323a298abcea5b2a65d29580517bdbc256c5", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_25 = ZkmUtils.getHashedSystemProperty(
            "0aea367a9bca395f5dedc96cfefae49574daec18d44d392377db95046a48c7afb6d62500c094e6e85eb2ffdcf6955503cc8ffce89b4217285fcfdfecccc6ad63"
    );
    public static final boolean CANARY_PROPERTY_26 = ZkmUtils.getHashedSystemProperty(
                    "b6339832be43bdbd54f828340aff71656d0b5267fb022c13ed2ed9aab64271683c7f515da2f65002eb03b1b9ad7de5ef569e214cf4df5ad0a31df4e65c1eda27", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_27 = !ZkmUtils.getHashedSystemProperty(
                    "4eba953e0c5ca1e3a5b3776764aa973ad46e0458fb7fc18cf3bd40829aa2f0a909b2f9a8aa821e932238588a8f46ad4bbed4de02fe74ca62c2e70db1fd2c60e2", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_28 = ZkmUtils.getHashedSystemProperty(
            "af5f470d8fc173b86131412e388ae11d6de268af6f2b3168e83b1459b7ffba1e0e6a2dd15c9cbfef7132330962e553717e314bc8bdcd21893d34e89d0d30a96e"
    );
    public static final boolean CANARY_PROPERTY_29 = ZkmUtils.getHashedSystemProperty(
                    "5e2a27b75091da608f80ccfbddaa4dbe9ad3a57e4bd28b839f3466a9b414c66c72ead0774e78709728c15710f58d89779e0646f006e5a9cb97aaf8be4fcb2c7c", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_30 = !ZkmUtils.getHashedSystemProperty(
                    "de44fed58a895e9bb53194329665109c1c368cc4949fc4a719eb5734d1fc84c80ee41d05a1dc1ab18dd8a75f0cee5701d5ab4d2910b3a0880b4be514f1d8aade", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_31 = ZkmUtils.getHashedSystemProperty(
            "bb5efcdbceb93fda951abbdf391250b48af8717194c8e1f16bddcd3b5a57c244c6cd2a988cb63d255d23347fe52969f15985deffa4d1449714c5886f63ec8f63"
    );
    public static final boolean CANARY_PROPERTY_32 = ZkmUtils.getHashedSystemProperty(
                    "17a83e0a39f45bf97e24f37e39a9ef3e6042a8ad47fa1efe9e496cd1c200b459f3cf39c5729597f3fbf6daa8f2315303e821bffa77829d734f80cf157a12b552", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_33 = !ZkmUtils.getHashedSystemProperty(
                    "7cb54f0a76ad4ebb9e679ed35f1388f338a2ba6a6a0ce2a9da1718a131a0ec47963bfb0c037650c9679e59ad915e596e1cb9e06102d5c4733cb2fae9d889814b", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_34 = ZkmUtils.getHashedSystemProperty(
            "8248b8fb60bd01402fc59bd66b9b2d267696d2c9ddb8e25d1be9b44043c1c966db6adedbb58a8f85a54038d7045208828c0bcc43d4505a353eaa65946a6162b2"
    );
    public static final boolean CANARY_PROPERTY_35 = ZkmUtils.getHashedSystemProperty(
                    "ce1e55ce1b2f453ea2d90fb0354c537f86f502761a9c6cd62ee48a145fc379691e03aa2f2c446472b677e73eeae4213e4d9baff13d9a149dc6bbfb0192237539", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_36 = !ZkmUtils.getHashedSystemProperty(
                    "78fa658eb8870fda2f4805465a3caca95caed17cd6b801df3fad05484906a94c8d919a3d1d7fa4ec1b9d145c3328e0a5d68de6fa8a75f383a354f82e0af63446", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_37 = ZkmUtils.getHashedSystemProperty(
            "f2cfd0b6ff14d3e5fd2461141c72f9a3f5e5f5a9814625d1d3d037acdd15a336c344b5793c3d3679c6c55f6edb1e3624afcc8e8a7c0ed4d3303070db866d0452"
    );
    public static final boolean CANARY_PROPERTY_38 = ZkmUtils.getHashedSystemProperty(
                    "3f629d3a613910bca96ff3e01813c4f754d97e93f098152de91880dd69b2e4b6589e8230a50c141b80d6ad6896b8c32bf1bef568cf63a627cd9381751d4c5b71", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_39 = !ZkmUtils.getHashedSystemProperty(
                    "2a4e7bcef3c448b3216d2b84e98f977ee5aa3439210027e89d0a8e4e2b1fe7ea0cf1ff184a1f37a8811210968829cfb285c069336decc40ba427b942b6210b36", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_40 = ZkmUtils.getHashedSystemProperty(
            "d1d86825577a65b0879e06bd09b6d954f93c0fb7f6392906a4af0b0b76d87e6a2f9fab65343298d1f9cccaa47db0237167fadc054b2901c935699d691bf888ee"
    );
    public static final boolean CANARY_PROPERTY_41 = ZkmUtils.getHashedSystemProperty(
                    "34d2527ce68f682562dd0ef54551c5e638c75c9d1caa33df935f7c57cb1d80f5d1dd97e856fbae0e4cfc36d836592fd904b97af4344269092746e273cbfeaba5", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_42 = !ZkmUtils.getHashedSystemProperty(
                    "8e5afe1ab98a4b953d40d1b0f23b0b5b7095b20b83b52d339747e794fb26733c50130fb0f3d674463188d82df4df3a1b4eb0c1c105efecfb3274f8344541e055", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_43 = ZkmUtils.getHashedSystemProperty(
            "496c8cfbb8b4e9a908d526c9e450945f5bf952d437632dc745590c36f40e2484e6b682b262603c513807c7c956cd3bf30d2ef22c7b01ef972cd3b054b92dde06"
    );
    public static final boolean CANARY_PROPERTY_44 = ZkmUtils.getHashedSystemProperty(
                    "1dbb3ad225d1c95af97f0bbfad260b6d0cf9a912b53166799cae436274fbcda53fce3d755354b63b8937b1733ee4a8fa3eaae4fee6a41f737941ef676951badf", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_45 = !ZkmUtils.getHashedSystemProperty(
                    "ff2e4add696b467041a7afc180a0c36b0e53f00ca1b76a5f563f413666b01405725eba438484c4f041bc329663128bf5df114bef889184438e149a49b972f5cb", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_46 = ZkmUtils.getHashedSystemProperty(
            "cef848e8efdef52221a466dc63e1c093d34611ff05e71c9c2c247b078a5c88622ba4d8ca3078307cfc30385e8916dfc3bf92d22ad3cbba5a05a90425fb78fa38"
    );
    public static final boolean CANARY_PROPERTY_47 = ZkmUtils.getHashedSystemProperty(
                    "cc1a78afeb1676eb565e6583afb2d7e11339298e48ebca14ec6a2250139cfcc2427df0048d0111cc388dd97e00645110c3306dc93633d156aca5f312b3cf26c9", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_48 = !ZkmUtils.getHashedSystemProperty(
                    "26f7b0061a80e03394e811632bc0a0dbcbada29cbf124e9dfb6590c89c40bbb1b4fafc067d540c593c3e08c30074a3337da57a94f380a8431faca293658cb393", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_49 = ZkmUtils.getHashedSystemProperty(
            "2ddab528ff90c09dea9e5cf639e4d0e21d7053a131ff91271f57810a6c9fd11509de13d3653b0e928f57663bf9d77bea9472fc9d701cd23c28a2191fcd2be039"
    );
    public static final boolean CANARY_PROPERTY_50 = ZkmUtils.getHashedSystemProperty(
                    "57e113d6d7de200039712287c5363bea2aebfdc858a5d7c64d9853703a90d49e2c17bda0c91f4c5db0a50ca0d3ee0161d9984cf8f2738abe8b369679619917be", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_51 = !ZkmUtils.getHashedSystemProperty(
                    "af7e850d6c895d0776a810640ebb6b0d0a0df3cd860d0f24d39b7bfe8c25bfe1f6a610a0c377757a8eb786bb849a187f0d4468cd003fbd9da18e776fba5cc8c7", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_52 = ZkmUtils.getHashedSystemProperty(
            "3c2c2741cfbbfe15abe0aa54ac53b77eb6cb7c00bab0e8d16d506504423c6e1c3601c5a76192c933c28503fd0fd325ff4e0e04ae30e9c789f0fbe84f5ee91723"
    );
    public static final boolean CANARY_PROPERTY_53 = ZkmUtils.getHashedSystemProperty(
                    "43024237981e11cc5c3d6c22a1759fc9136053a6ef0c860abd3c7d9ab87c9cc862c71527f8ab3526f07d97ce0b1aac954fc027e2c27e8e536001c0d9726886d3", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_54 = !ZkmUtils.getHashedSystemProperty(
                    "274783cbd9ce9a046c3ecaaf86603905b3fd55fd83e73b065fb38b517b2f993dfff3a1784ecd6b69b985ca38ff4d19636607e7c89e0c6f6794f1fe3aff5e0a0d", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_55 = ZkmUtils.getHashedSystemProperty(
            "9d486c1b1bf58f9a27950e9b3d079e8b2dc2792894b90a3022ad95a45b9af349723f3bd45489f8695f41ba3b49986497982ccdbd4d113ec8f10751aadd4e94ba"
    );
    public static final boolean CANARY_PROPERTY_56 = ZkmUtils.getHashedSystemProperty(
                    "1b4812881cd571b129b638ffe2cca4dbabcbafe649fd76c0494bac122085058a577f8371f99cc209f2cbd628cfdacfa523203684fc5650051a762d9b90b475c2", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_57 = !ZkmUtils.getHashedSystemProperty(
                    "ce181ef9037e2820dca7b0f7f806ed427df448684ae8cc0472af6c35be225371d0818273d58c0fafba5ee1b9a2397c72033b37e3d8fb151fc9e24fc9a2dbea98", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_58 = ZkmUtils.getHashedSystemProperty(
            "ed7a47c468acb2255585731409cf72e0616eed0e6b5ec3aae63d234b5b7252902eb6922c11557359e02877989ad739a61a8e8f310b00dbd1df958cd6bb7e9f3e"
    );
    public static final boolean CANARY_PROPERTY_59 = ZkmUtils.getHashedSystemProperty(
                    "ba3b47a10bba416b4fee374207ff57f7b4a68b5b48c1dc31eafebcdbfcb72b1451b12883422aeef1089b15bd87cb3390de89d7f3d61a861f70fc62b826cbfd41", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_60 = !ZkmUtils.getHashedSystemProperty(
                    "cba0ec2bca2c40cf190ed7f4c9ed2cefa4206706f5439442d4d4b1fa5ff3d565e1190fc0096bd78c8cff9cb5f4d8fd9fc8c623f1c6c14d837956cb47b5d244fb", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_61 = ZkmUtils.getHashedSystemProperty(
            "93a74207419df2b9a2ef22b39538c63994072ab90a1e6f94a31094774c17469d7d12a219557d7e801150fc8e89559fce04abf1ee5699affedabc101a4607a6ea"
    );
    public static final boolean CANARY_PROPERTY_62 = ZkmUtils.getHashedSystemProperty(
                    "56eb0107a89e870c9656a2897895da189002f1f2021b636e899412be25523038849b18ecafeb6eed56fe73a4197f60ca36960e63e44b7acd86f400fa8ea1e570", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_63 = !ZkmUtils.getHashedSystemProperty(
                    "10d98c0e47daf2efa85fc925bb9ce9e4427fdd63c71ee40efa261bafa11c0bab8a1eeb8935632585a79078daf0388f6f3c48fe883d0f683925a217b908e7c858", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_64 = ZkmUtils.getHashedSystemProperty(
            "8b2715aba85a5e03c52a027f6b092049761701dd576ff4abd301e43536d618bedb1d78ca644f519540f74e4914d5ea8ede8b7c20ca2f90a71e1250d70f38d6c4"
    );
    public static final boolean CANARY_PROPERTY_65 = ZkmUtils.getHashedSystemProperty(
                    "2f4d28a8c5579be417b1b9c6fc6ad7c46d6cf3b5f71ca98662a0d70051d1e966881ef5d7f6f18b4cad6d281ab7486bccb8ddd4ccb28507fac4e2aa63e91ba08f", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_66 = !ZkmUtils.getHashedSystemProperty(
                    "950f1be6bda455568ec91a61d488cd2c4af1051c1ca065b12078603ce7478c46ca43472102c3e7a792509b364f0180e31c3ae2d673d2c56212f49e46bae9955f", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_67 = ZkmUtils.getHashedSystemProperty(
            "ee3e241af6e1c26736a17bfb6c4a73e1b0da55cb9647a0771de2e19ce563b229a52d1a29cb48330732b1a65f28768738e046cbdc302dd893109c1b8509c51cd9"
    );
    public static final boolean CANARY_PROPERTY_68 = ZkmUtils.getHashedSystemProperty(
                    "b6f6fa8731f8706620970155f7d781378f6221bd4ceec489839f08c54319ae3f4964e48cd2a579512727575c2d90a04d6d6d0f37f08e273a26c92f529262372f", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_69 = !ZkmUtils.getHashedSystemProperty(
                    "d5ebf0a2ccfd6f12f57f08588ab4f000d553d2f53757ce3e715f6bc4f170b82fe8470c16288a68afaf7b4861420481529d2fce2a7632d0c5515632ac87bf0ba5", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_70 = ZkmUtils.getHashedSystemProperty(
            "a3ba7b88b0fa44b7b225ab7c47fb6c03871678f42348b5256eb18c99c562773efe8e01f97098345fc084aeb4ce5e529f6f297e6ea904ebfd0c64b5e0be6f39ac"
    );
    public static final boolean CANARY_PROPERTY_71 = ZkmUtils.getHashedSystemProperty(
                    "8b0c719ba1358dc4110f60c253719674c85cc9af03ffa8dc7c2bbc98dff07b93c1f6794974147d900d2c98a93f462b075b25d56db3915c6ab1f7cb2022849a4a", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_72 = !ZkmUtils.getHashedSystemProperty(
                    "8946fa91f9d401367fcea387a483367abaa34a1b8ee7797ec391ad587ec1beae1813765d658d67d9045a74b793bc77578f3e47fa8c4b813d853c34e0dd327d12", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_73 = ZkmUtils.getHashedSystemProperty(
            "f1fc1bf28b544e7bdb59739156674333330d956551ad8fabd4056e527d5688be21a161b1b0d8b19cecd1d80e08a5a3d2e9049bacb3e53a7f3ee76ecdb39ed458"
    );
    public static final boolean CANARY_PROPERTY_74 = ZkmUtils.getHashedSystemProperty(
                    "e4487b3b7f53867ba8159fa8ca40d34ff72b2c91c81572cb4315e3caa2138ef9c3d86b84165a17e24b588a50638b33ace6a88c81e3e371b86c9bfbaf06bafb75", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_75 = !ZkmUtils.getHashedSystemProperty(
                    "e4eb491ad241f965cffccfdee6a51a1a074656437fd550501ed42325bcdb9b24341b2764da5d1ac591b5d6933667e3a2233c01f343b21df9e0d12f8859fca5ac", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_76 = ZkmUtils.getHashedSystemProperty(
            "29a67b0a658a10fc69fe992d5521edaa8ac2464261b350f28f912dfdc63aeae0ed04c141c660ccd27aa83968c8a88dad1bd9abd373982ceaf3736b264092cbc3"
    );
    public static final boolean CANARY_PROPERTY_77 = ZkmUtils.getHashedSystemProperty(
                    "2b4884fab6b8b06976e43470d08440ad70975ba1a2f57cee77e7195614843a423ee94d69430e8ce3d34de75439d482e8a37b17c046ba2d6c360a464f8b9903c6", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_78 = !ZkmUtils.getHashedSystemProperty(
                    "6a2134bd704e6c121d4bae9150791ea03d6b410af562fec3bfb8e05e29fe655b153421c2993427fd694b90cfe174e8bda3341aa98e52a7ed3ae769c8973baf6d", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_79 = ZkmUtils.getHashedSystemProperty(
            "ea5e8896f1a7abd0d2ceb5aac7255eae446dbe75bbf0c6cb5175d4e916536addd7acc18ec92abd77740eeb55a77eff190644f72f5938a3e03c2dedb1b103f931"
    );
    public static final boolean CANARY_PROPERTY_80 = ZkmUtils.getHashedSystemProperty(
                    "72926a7086b523363c332ee616218f0f4b3ce70c0e4a944758961c50ae1691891bb7c30fc2608611ffa523709634fc948e054de25f18cde5387e03fce2faa4dd", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_81 = !ZkmUtils.getHashedSystemProperty(
                    "22bbf731f2b662b911a310f774025cd719b821e0fd653dd0a2c165572d4077b4e5e8e41102ba5856d01ef255e2771192fdc259cb3f466544a4cac2aa23c215f1", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_82 = ZkmUtils.getHashedSystemProperty(
            "e1fb5cae89638cddb24618194fe16e817eb46a3239598a876f2c615d5869c9dff73739a0499e07809eb7e6d22f3d8abf8b42ea23a4471c5bcce323c1c51f7973"
    );
    public static final boolean CANARY_PROPERTY_83 = ZkmUtils.getHashedSystemProperty(
                    "409550d8083b2e4814293f10d86daf5ad358a635d20aeaeafa166e3257a5322bdb8f403b64538817b0d8e2061919b4adfba956c6712e40ef64eb26582c364158", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_84 = !ZkmUtils.getHashedSystemProperty(
                    "263a29e1e4dce70e5788fe077c372a6f03f4b4787598d3f06d361c82930d369f8a80c94fc5ef9d6d8bf0a03b2ddef97056d5ec68a577efbf493067c772342f88", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_85 = ZkmUtils.getHashedSystemProperty(
            "1399f491b413d3933ebe7ad26e0afed0b756aaaafcd1666ec16145a2bc23f9fd3b450945762d51b45b96e8ac65eae1d6ad5871a63307ae06f3df0d09ba457bdf"
    );
    public static final boolean CANARY_PROPERTY_86 = ZkmUtils.getHashedSystemProperty(
                    "e5f648a7562b90ddf9afb8a4ffb7a6fd6557217f0c3372915d661d32ac4e4fbb76327d071e95f59e09292cf4c2a264274c13b5ca0e2170854bc600f54dbdd50c", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_87 = !ZkmUtils.getHashedSystemProperty(
                    "9b5fdaeb47f1cd299ff349f9ff00e5f42076bf46b41178970f33a3880c4472f437bae83964058bcd2b835d4218c21081b42e4223f255eff55438bcb527d293bb", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_88 = ZkmUtils.getHashedSystemProperty(
            "1b786a994daefac7ed802b2f5bc3c07ea420c5629430334e0e34669ddf8c89c4880f1204e98014daf9c53be68c108fae38547573150564ebe6495a1694274717"
    );
    public static final boolean CANARY_PROPERTY_89 = ZkmUtils.getHashedSystemProperty(
                    "dced2da148261a1f0df7814d5bc2a83dd794a3d65477d20268e2c77c118d4cd4d858768d19cc5a6280a193965c7522197fce69923920002cf7836bac81f29f76", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_90 = !ZkmUtils.getHashedSystemProperty(
                    "853739f75fb06d51b78d01d6f7ce1c1026c6f745259d8cf7a696a2e30c0061fd41b05618356810e23cd10a03f19e533833e49aa20af8419e25878df7c21ed385", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_91 = ZkmUtils.getHashedSystemProperty(
            "510cdd04df82bcfe5a59ab991e76fc4883d404848bf7c5d1c053700f04c3ed705ca3d8ff058254d4ba4f152bfb7dee8343f494dfad7bb5210a1cd04c542e631c"
    );
    public static final boolean CANARY_PROPERTY_92 = ZkmUtils.getHashedSystemProperty(
                    "5a529dc5d5d87e137c91ecd3a6f9cd60a9533e37ea330c862f87a9fea863dbd6cbce0e54567f01a4ad6959e650aa81dff6adb8bbd384d2630bc757ca59ebe228", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_93 = !ZkmUtils.getHashedSystemProperty(
                    "0450e7c9852a6cd85c79e7d6bd42d59369d2acd3d1f1cfd6b6c75dbd6045780eedfe90bdb4a5d68c93cfd79d5c372159782e0208d3c7963589cbf3392cb42ba8", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_94 = ZkmUtils.getHashedSystemProperty(
            "b1c808e080f9eca0abdb8e866563897b245ceb85c15a62ea0cb3169fb974052ec767769f0290cc78f634998399f2485803d2a0bfe98e6f0c819b19df4b2664cb"
    );
    public static final boolean CANARY_PROPERTY_95 = ZkmUtils.getHashedSystemProperty(
                    "c78e30fc29f4a9d37d8e57d5edbf0dc8014cddb86939bbf6551ccd7d08bec433624e8534746d78c1e238354aca5d9171d1b6dcbd95ae6cb2b367348bced4042a", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_96 = !ZkmUtils.getHashedSystemProperty(
                    "116ca1d506d52a28d5e9433825a7063fc0f621a78ff0b949d0bd221bdfa5893eaebdef7ec7067156fbe025703791c7f27206bf32e4aac888d7bd8c2499a074fb", ""
            )
            .equals("false");
    public static final String CANARY_PROPERTY_97 = ZkmUtils.getHashedSystemProperty(
            "7a5b9ca1eb2e2924d8db9cbdb1e177ff18c7df34b853ee2d3c31137cda0bafccdd1187030d7873a392e2f9ba6b158f3fc72fd9cfc84fb2ea5acf22229c9f0681"
    );
    public static final boolean CANARY_PROPERTY_98 = ZkmUtils.getHashedSystemProperty(
                    "85ba39f068cca5f1f542ac685ab11a7a6166e9d4a6813d3c4e2445d502b99d5127431b7216eb2cf2cedf9e5389ef0159f136ca2838edd78d9b69196ba6cd8ce5", "false"
            )
            .equals("true");
    public static final boolean CANARY_PROPERTY_99 = !ZkmUtils.getHashedSystemProperty(
                    "574d61836e0d2ee45ae3069ed1f6f8a640681f178f96efc3927ac37816c66f75b09129aa925593d92b23e22e07dc58d07fe02caa44ab0983a58352714f6a1271", ""
            )
            .equals("false");
    public static final String NEW_CHANGE_LOG_ENCODING = ZkmUtils.getHashedSystemProperty(
            "5ac3af378a1f76f234fcaf85bee2f8cefc88b84ab72edc4f6f55ca4dc8cd2139843c83e92b1e83d7a163143d57c54e53fa92a69d09b429493371907080634f15"
    );
    public static final String JAD_ENCODING = ZkmUtils.getHashedSystemProperty(
            "35bbb8a793517e8f0da520c0390454b37da96190ff1a8568061969ab8fee833d892a7c161b6531e55b3090c968fc8102ed20c61d88c05589f584604054d30c61"
    );
    public static final String SCRIPT_ENCODING = ZkmUtils.getHashedSystemProperty(
            "5b379c316876954087e761c9fe1ed705a6ec8f3e042aba396311f41bbeda6d78dc8cd0b1aef399788389c10f786be2ce122c9857456967dd2e969936769f3789"
    );
    public static final boolean RANDOMIZE_OBFUSCATION = ZkmUtils.getHashedSystemProperty(
                    "ea6de8c7d94a37e373802bd5c29a5561f5e033f26709fe7b19675dbeb5f60b7c6f58ee5c8001f85fab5feb57529b755c3a6b3c7278e1ae94f8692b19866686fa", "false"
            )
            .equals("true");
    public static final boolean MARK_RENAMED_LOCALS;
    public static final boolean VERBOSE_STRING_ENCRYPTION;
    public static final boolean MARK_ADDED_MEMBERS_SYNTHETIC;
    public static final boolean RENAME_TEMP_OUTPUT_FILE;
    public static final boolean FIXED_TOTALLY_UNCHANGED;
    public static final boolean REFLECTION_WARNINGS;
    public static final boolean TEST_HIERARCHY;
    public static final boolean ALLOW_RENAME_MAIN;
    public static final boolean FIX_MANIFEST_MAIN_METHODS;
    public static final boolean DELETE_EMPTY_DIRECTORIES;
    public static final boolean APPLY_CHANGE_LOG_EXCLUSIONS;
    public static final String EXCEPTION_OBFUSCATION_USE_EXCEPTION;
    public static final String EXCEPTION_OBFUSCATION_EXTRA_EXCEPTIONS;
    public static final boolean FLOW_EXCLUDES_EXCEPTION_OBFUSCATION;
    public static final boolean SHOW_EXPIRY_ERROR_CODES;
    public static final String STACK_MAP_ALGORITHM;
    public static final boolean FORCE_FULL_STACK_MAP_FRAMES;
    public static final boolean REBUILD_ALL_STACK_MAPS;
    public static final boolean IGNORE_MISSING_MEMBERS;
    public static final String AUTO_REFLECTION_MIN_HASH_LENGTH;
    public static final String AUTO_REFLECTION_MAX_CANDIDATES;
    public static final boolean AUTO_REFLECTION_METHOD_KEYS;
    public static final String RANDOM_SEED;
    public static final boolean DEBUG_LICENSE_CHECK;
    public static final boolean PROGUARD_STYLE;
    public static final String DEFAULT_DIR;
    public static final boolean MARK_GENERATED_CLASS_NAMES;
    public static final boolean SKIP_REFERENCE_INDEXING;
    public static final boolean ALLOW_DUPLICATE_MANIFEST_ENTRIES;
    public static final boolean RESOLVE_REFLECTION_UNKNOWN_ARGS;
    public static final boolean PARSE_ONLY;
    public static final boolean FOLLOW_MANIFEST_CLASS_PATH;
    public static final boolean DONT_ADD_WATERMARK;
    public static final boolean USE_ACCESSED_CLASS_FOR_REFERENCES;
    public static final boolean ALT_REFERENCE_LABEL_PLACEMENT;
    public static final boolean OBFUSCATE_ALL_MEMBER_REFERENCES;
    public static final boolean REFERENCE_OBFUSCATION_METHOD_KEYS;
    public static final boolean STACK_MAP_USE_ALL_LOCALS;
    public static final boolean KEEP_UNCHANGED_MANIFEST_ENTRIES;
    public static final boolean TEST_JVM_VERSION;
    public static final boolean DEBUG_REDIRECT_OUT;
    public static final String AUTO_REFLECTION_OVERRIDE_INDEX;
    public static final String AUTO_REFLECTION_OVERRIDE_LENGTH;
    public static final boolean ADJUST_LOCALS;
    public static final boolean ALWAYS_CHECK_RESERVED_CLASS_NAMES;
    public static final boolean USE_BOOLEAN_IS_GETTER_TYPE;
    public static final boolean STRING_ENCRYPT_INTERN;
    public static final boolean OBFUSCATE_REFERENCES_INDY;
    public static final boolean OBFUSCATE_JDK_MEMBER_REFERENCES;
    public static final boolean FORCE_FULL_SIZE_FEATURES;
    public static final boolean DISABLE_FULL_SIZE_FEATURES;
    public static final boolean NO_STRING_OPAQUE_PREDICATES;
    public static final boolean TRIM_XML_TEXT;
    public static final boolean COPY_ARCHIVE_DIRECTORIES;
    public static final boolean TRANSLATE_PROGUARD_UNEXCLUDES;
    public static final boolean SEVEN_BIT_STRING_KEYS;
    public static final boolean FIXED_PARAMETER_KEY;
    public static final boolean CHANGE_LOG_PARAMETER_DETAILS;
    public static final boolean DEBUG_PARAMETER_CHANGE_LOG;
    public static final String CLASS_INIT_SPEC_FILE;
    public static final String RELATIONSHIP_FILE;
    public static final String FALSE_CLASS_INIT_SPEC_FILE;
    public static final boolean STRING_ENCRYPT_DES;
    public static final boolean STRING_ENCRYPT_INDY;
    public static final boolean INTEGER_ENCRYPT_DES;
    public static final boolean LONG_ENCRYPT_DES;
    public static final boolean INTEGER_ENCRYPT_INDY;
    public static final boolean LONG_ENCRYPT_INDY;
    public static final boolean ALLOW_TEMP_FILES_IN_CLASSPATH;
    public static final boolean ASSERT_SPRING_BEAN_CLASS;
    public static final String LAF_NAME;
    public static final boolean REBUILD_PARAMETER_MAPS;
    public static final boolean MATCH_ORIGINAL_NAMES;
    public static final String METHOD_PARAMETER_MAP_CLASS;
    public static final boolean USE_PARALLEL;
    public static final boolean SYNCHRONIZE_PARAMETER_LOOKUP;
    public static final String EXTRA_XML_FILE_TYPES;
    public static final boolean TRANSLATE_KOTLIN;
    public static final boolean MARK_OBFUSCATED_PARAMS_VARARGS;
    public static final boolean REMOVE_CORRUPT_CLASS;
    public static final boolean PROCESS_YAML;
    public static final boolean PROCESS_PROPERTIES;
    public static final String PROPERTIES_ENCODING;
    public static final boolean DETERMINISTIC_OUTPUT;
    public static final boolean ENCRYPT_TRIVIAL_INTEGERS;
    public static final boolean ENCRYPT_TRIVIAL_LONGS;
    public static boolean skipFrameCompatibilityCheck;
    public static final String FRAME_CHECK_MODE;
    public static final boolean FOLLOW_LAMBDA_METHOD_HANDLES;
    public static final boolean PRESERVE_ZIP_ENTRY_TIMES;
    public static final boolean NO_RANDOM_INSERTION_POINTS;
    public static final boolean INSERT_AT_METHOD_START;
    public static final boolean SKIP_EXCEPTION_HANDLER_OBFUSCATION;
    public static final boolean SKIP_STRING_CONCAT_ANALYSIS;
    public static final boolean SKIP_STRING_BUILDER_ANALYSIS;
    public static final boolean MATCH_UNRESOLVED_SUPERTYPES;
    public static final boolean SKIP_SUPERTYPE_MATCHING;
    public static final boolean CHECK_REPEATED_PARAMETER_CHANGES;
    public static final boolean ALWAYS_REGENERATE_PARAM_LAYOUT;
    public static final boolean PREPEND_PROGUARD_CLASSPATH;
    public static final boolean STRICT_TRY_CATCH_ANALYSIS;
    public static final boolean FORCE_BASIC_SIZE_FEATURES;
    public static final boolean DISABLE_BASIC_SIZE_FEATURES;
    public static final boolean NO_REFERENCE_PARAMETER_KEYS;
    public static final boolean KEEP_UNUSED_PARAMETER_SLOTS;
    public static final boolean ALWAYS_TRUE_OPAQUE_PREDICATES;
    public static final boolean OBJECT_OPAQUE_PREDICATE_TYPES;
    public static final boolean ABORT_ON_PARAMETER_CONFLICT;
    public static final boolean SHUFFLE_MEMBERS;
    public static final boolean CHECK_SMALL_CHANGE_SETS;
    public static final boolean NO_AUTO_REFLECTION_METHOD_KEYS;
    public static final boolean SIMPLE_PARAMETER_KEYS;
    public static final boolean SKIP_PARAMETER_MAP_REBUILD;
    public static final boolean SKIP_HIERARCHY_INIT_ORDER;
    public static final boolean SKIP_SPECIFIED_INIT_ORDER;
    public static final boolean UNLINKED_PARAMETER_CHANGE_NODES;
    public static final boolean NO_REFERENCE_METHOD_KEYS;
    public static final boolean NO_METHOD_KEY_FLOW_OBFUSCATION;
    public static final boolean XOR_INDY_OPCODE_KEY;
    public static final String PARAMETER_KEY_SIZE_OVERRIDE;
    public static final boolean CHANGE_OVERRIDDEN_METHOD_PARAMETERS;
    public static final boolean NO_METHOD_HANDLES_LOOKUP;
    public static final boolean SKIP_UNRELATED_PARAMETER_CHANGES;
    public static final boolean NO_JAVA7_INDY;
    public static final boolean ALLOW_HASH_NAMES_IN_NAME_FILE;
    public static final boolean DONT_SHUFFLE_REFERENCE_OPCODES;
    public static final boolean SKIP_PARAMETER_LIST_REUSE;
    public static final boolean STRICT_CLASS_INIT_ORDER;
    public static final boolean RANDOM_CLASS_INIT_LINKS;
    public static final boolean NO_CLASS_INIT_LINKS;
    public static final String EXTRA_PARAMETER_ODDS;
    public static final String EXTRA_PARAMETER_COUNT;
    public static final boolean SKIP_BOOTSTRAP_METHOD_PROCESSING;
    public static final boolean LARGE_PARAMETER_KEYS;
    public static final boolean NO_PARAMETER_KEY_SIZE_LIMIT;
    public static final boolean RENAME_PARTIAL_DOTTED_STRINGS;
    public static final boolean DONT_RENAME_SLASHED_STRINGS;
    public static final boolean DONT_RENAME_DOTTED_STRINGS;
    public static final boolean TRANSLATE_KOTLIN_METADATA;
    public static final boolean LOG_REFERENCE_INCLUSION_MATCHES;
    public static final boolean DONT_SHUFFLE_CONSTANT_POOL;
    public static final boolean DONT_SHUFFLE_PARAMETERS;
    public static final boolean DONT_SHUFFLE_LOCAL_SLOTS;
    public static final boolean LENIENT_TRY_CATCH_ANALYSIS;
    public static final boolean OBFUSCATE_INTERFACE_PARAMETERS;
    public static final boolean ALWAYS_ADD_EXTRA_PARAMETERS;
    public static final boolean DOT_MESSAGE_SUFFIX;
    public static final boolean DONT_SHUFFLE_NAME_CHARS;
    public static final boolean KEEP_FIELD_ACCESSOR_TYPES;
    public static final boolean ALT_METHOD_NAME_CHARS;
    public static final boolean ALT_FIELD_NAME_CHARS;
    public static final boolean SINGLE_REFERENCE_PASS;
    public static final boolean ALWAYS_INLINE_INT_CONSTANTS;
    public static final boolean IGNORE_METHOD_SIZE_LIMIT;
    public static final boolean USE_PLAIN_RANDOM;
    public static final boolean NO_COLON_SPLIT_XML_NAMES;
    public static final boolean KEEP_SOURCE_FILE_NAMES;
    public static final boolean FORCE_SKIP_FRAME_CHECK;
    public static final boolean ENFORCE_FRAME_CHECK;
    public static final boolean LENIENT_INTERFACE_SUPERTYPE;
    public static final boolean NO_VERSION_STAMP;
    public static final boolean NO_TRANSITIVE_INIT_ORDER;
    public static final boolean RENAME_FILTER_ERRORS_NOT_FATAL;
    public static final boolean SKIP_INHERITED_FIELD_NAMES;
    public static final boolean SKIP_INTERFACE_FIELD_NAMES;
    public static final boolean STOP_AT_FIRST_SPLIT_CANDIDATE;
    public static final boolean STOP_AT_FIRST_BLOCK_SPLIT;
    public static final boolean NO_OPAQUE_INSERTION_SPLIT;
    public static final boolean NO_KEY_INSERTION_SPLIT;
    public static final boolean DONT_UNESCAPE_NAME_FILE;
    public static final boolean VALIDATE_NAME_FILE_IDENTIFIERS;
    public static final boolean TRACK_ALL_PARAMETER_USAGE;


    static {
        ZkmUtils.getHashedSystemProperty(
                "7edb1c4f2b322ccb0fb588b5c5203214bf3b90ca86397577cfeb060109212e173f8baad8d9f41d6f26cd7c227a2ed69e15664d7cd43693ec84faefa23643dc6c", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "d38cab9fa3a261ceaf6eea51eaf3ac4ffa2d6aeeebc3402e4f3e91c07c672aaf3cf4577a18d820b761c856b3fd31557d2023e2ec21ba903e7bf21eb8c7bf8de6", "false"
        );
        MARK_RENAMED_LOCALS = ZkmUtils.getHashedSystemProperty(
                        "41ca8705c1cb49d968c0af5ff3b01c763d6706c789e56d4bb5b64e7ab778335946c3269cf08aa85e398b84edfab1e7ce3f502c0a7d0649f1ffd3071c7f38c94a", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "0a6330b900823a495c61107351b33802c86c70f19e29bec81f05c7737e92f70530873e4cadc2cc243011e06cb4d03dde9254aa695634c6c5b4a9c1c2ce4e6a20", "false"
        );
        VERBOSE_STRING_ENCRYPTION = ZkmUtils.getHashedSystemProperty(
                        "8caefc284c5ffd8948bbddc483c95451df45616507ff9ba1607f847f5250af845a381646030b32e4ea7c76cd19e40b31c48692a40349866e3580985b201067a8", "false"
                )
                .equals("true");
        MARK_ADDED_MEMBERS_SYNTHETIC = ZkmUtils.getHashedSystemProperty(
                        "6a025ba242cba58eddb5f91aac17a24ced9b19fa92513d8e3f527de4872dfe64b2d038919fed416c87e52af81db0975bdd0753fccac96a7426ea7b884edaa3f5", "false"
                )
                .equals("true");
        RENAME_TEMP_OUTPUT_FILE = !ZkmUtils.getHashedSystemProperty(
                        "009ca4a115abc39cb5fbbd43be46e2f32ba7574b9fa8b2577a892251c12d6c116f18af1c4ad34674b721f8bdf5f034b5958067a0ce9cb2dc062d3b77b6826897", ""
                )
                .equals("false");
        FIXED_TOTALLY_UNCHANGED = ZkmUtils.getHashedSystemProperty(
                        "22bcbcd0fdb7887ae0f4526a4997393c9790c22900572521a5d5411100f6c8e165a88aa40538a86babe973abee45e3e913e4b8662413e970285fdf5d03607833", "false"
                )
                .equals("true");
        REFLECTION_WARNINGS = !ZkmUtils.getHashedSystemProperty(
                        "3f8d8478e330dcad6914faa6cc2bc3162175aa2e3fdf8faf4db35c1c50c31574111270699c684fc8997473d49fa6ba26644389387778f231a9a233e3e274f4ef", ""
                )
                .equals("false");
        TEST_HIERARCHY = !ZkmUtils.getHashedSystemProperty(
                        "53a9fe4e1d464298ddf67b1cb4b364997e1635e7cb9d0dfc67fcbdf8859e6df02608507ecd4871b1814ebe3614bf282b7e36774448e84e5a057f2af5fc92e690", ""
                )
                .equals("false");
        ALLOW_RENAME_MAIN = ZkmUtils.getHashedSystemProperty(
                        "09a0eded4e42f5c3c4d97866d1e3ac8d2c07db44e93e888b12afe081f2efa565e3f2a6b509d48eaaedbdc2702dcfe3bb63c3ff461415ca2232c2c5867aa04c44", ""
                )
                .equals("true");
        FIX_MANIFEST_MAIN_METHODS = !ZkmUtils.getHashedSystemProperty(
                        "402970e499e1a345dfac6cca6fd84aeaf79b4abe369782bcf47489c303098be60f3981957789214adb6b0b68d280f747be15f141d702059b5c6762e44e425653", "true"
                )
                .equals("false");
        DELETE_EMPTY_DIRECTORIES = ZkmUtils.getHashedSystemProperty(
                        "e2bc6a191dec4964e4b8218e637f063cd6119f7c707b170d035e204e1b5baad6a55e30781553255bd004c57d625a52a48ea637441a12baef470e40d4f6f085ca", "false"
                )
                .equals("true");
        APPLY_CHANGE_LOG_EXCLUSIONS = !ZkmUtils.getHashedSystemProperty(
                        "ca7a2e00e438b7202aff872e520b8f2c5599d1b193d612e61532ed168e773cf21594c03835e5f345b47b5e4e3a7c3250f54f87b7fed110d4c231769ee55868d4", ""
                )
                .equals("false");
        EXCEPTION_OBFUSCATION_USE_EXCEPTION = ZkmUtils.getHashedSystemProperty(
                "1b29989ee6484107d911d591f4311fe23001fe70e6b10b159674eeedfb8d60c105de2e4b21d0d9741c299b794a255ef06aef4f57897e189b5da11a6a2acb78ac"
        );
        EXCEPTION_OBFUSCATION_EXTRA_EXCEPTIONS = ZkmUtils.getHashedSystemProperty(
                "2d10624884df849bdda8397d3baf194c804f89b57ee419b010754e562931a19e6fe61758f72680a97d991aa8e26f6ed7564d6f9613bbfbf25e916edcae8e1c98"
        );
        FLOW_EXCLUDES_EXCEPTION_OBFUSCATION = ZkmUtils.getHashedSystemProperty(
                        "b5029b7ef4642c377addef9a3f3ed30e4bb2259638e797fa0d2f92d74a4f99b71e9a290176964e695c8c0eeb6edc08a67c042adc3eb5a980df3db3f6e96d1dce", "false"
                )
                .equals("true");
        SHOW_EXPIRY_ERROR_CODES = ZkmUtils.getHashedSystemProperty(
                        "b8288aa599af0d36d92e3c3f223af6b64ec430486783c933f4db81a72955c338dc7ed82607e30f855cd375cb4fc7ade0c00b686ca00040dcb015061bd69e7733", "false"
                )
                .equals("true");
        STACK_MAP_ALGORITHM = ZkmUtils.getHashedSystemProperty(
                "320b3f3135ed48d14e719bf8b4a70d3ce35ce4f5bcb1d30261658b4d87d1d427621def69871319bb9dfd93f6f188b9937e9691f03b42dffc22a0aa24c6ae7129", "0"
        );
        FORCE_FULL_STACK_MAP_FRAMES = ZkmUtils.getHashedSystemProperty(
                        "04863e64f8a70981ba4ea520739d520e07f0c8b7574b04edad570fda9a7881fe7e46a42100b11ae5de2bf7d0ae766b57546b6157c499dde03187aa9a3763a6f2", "false"
                )
                .equals("true");
        REBUILD_ALL_STACK_MAPS = ZkmUtils.getHashedSystemProperty(
                        "fb051fec4a63f6b776eea8c43911d4e3c49fecb080395a666cad5dff22c8141d04fff5bb92fa5d23b09efd541f794b7a46a978b47b3c7878f860df55a9c2f224", "false"
                )
                .equals("true");
        IGNORE_MISSING_MEMBERS = ZkmUtils.getHashedSystemProperty(
                        "c3e3da6dfc04c0061f11c67aa405992f2eff9fd9d91e3dfb968efea8c1ac4db0b08915854bd51118241ef6f730688b90a991cb450cb5c285f86337791953d5c6", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "62708020324bc42068323f1ba789ba689c16e10e03dd8f0b788d8a20a744618505d1c26a0aaf8ee848d7f0fe0f3770a02fb6109011d67db1ea3c79f6e9d6787e", "false"
        );
        AUTO_REFLECTION_MIN_HASH_LENGTH = ZkmUtils.getHashedSystemProperty(
                "dbbf2a46360c83c1e267929032998a58263f1fefe46d42aeb6ad99537e5ece878a35874b4b819c54fbae5b3d617c39c62c523ef2c6393845ed6faa29260d0d94"
        );
        AUTO_REFLECTION_MAX_CANDIDATES = ZkmUtils.getHashedSystemProperty(
                "95d6b799e642d8895963f08ce6df98a6c640a0fa6447860474a179b724998594959d17e8965990d248ce45ab53da75a5a0e922abde8fd4b9b79be509a4d2882c"
        );
        AUTO_REFLECTION_METHOD_KEYS = !ZkmUtils.getHashedSystemProperty(
                        "f888e8b13c3caaf2dbddc1a4dfdd78ef206399657e191409d075289b032ee8c1b117a61a8b8c433c0ea9b2083a6aea4c4b77dd63d698325cf14b70d04c6cc13a", "true"
                )
                .equals("false");
        RANDOM_SEED = ZkmUtils.getHashedSystemProperty(
                "9c6aa07bfdb5188e69d5e22c528ade723d655ecac87289a4053caad541710ffe04b85765690cda8076da09adb43dac6a3fcf4c5740399804d7ff2f98cd5890d3"
        );
        DEBUG_LICENSE_CHECK = ZkmUtils.getHashedSystemProperty(
                        "1d7803f5379c5e85eff22c072ce7dbc7610d39293c4b7db2d5f00480aed6252caac6bcf39e98b35a6a5e3a8356ebdb66a1300690056e7979e3c71ce459dff57f", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "c13ccc94347760bd8f2e2cbc1478773b3fe5e494370b633433e85b71a981bd4579b53c46e68aa7ca5513c4d5a5bbf36e87926a992cae0842d82f1bfe712a35a0"
        );
        PROGUARD_STYLE = ZkmUtils.getHashedSystemProperty(
                        "7f332084bcdad92af8485f3b485b17078bfe34370d75ae52dced9d0f532108e1d6a9373cbc0f0d5028025f56c99df5c6f2686eae97cccdc3d5cecc8209376ad2", "false"
                )
                .equals("true");
        DEFAULT_DIR = ZkmUtils.getHashedSystemProperty(
                "a7f508eeb1fd90ee8bad1fdce431714ec8e22d51aa08ffdaecb1db4526fad6aeaf6328a6bc2b96b42adee291a0852bad8f7e597c37e46918f8e647c2a4174368"
        );
        MARK_GENERATED_CLASS_NAMES = ZkmUtils.getHashedSystemProperty(
                        "109f3bee852d498531318089d48840df88ba30e409a3e4bec95e1ab719704922aa4aea79a553b7a4d94958ec247274bc039d1c419e93e41c325e3d22a64094c0", "false"
                )
                .equals("true");
        SKIP_REFERENCE_INDEXING = ZkmUtils.getHashedSystemProperty(
                        "58a15191485daa77b6600faa674e4eda1c896b543fd29ea6dcb413ed3f51766b6244a1421cdff95d21047eb5fc8861722aca1514287541d570fb84d8edcb73e3", "false"
                )
                .equals("true");
        ALLOW_DUPLICATE_MANIFEST_ENTRIES = ZkmUtils.getHashedSystemProperty(
                        "bed713aeb8269261a5703de9bd84e2f6bdca92fcfaabb45f2fc0675483c1a72bf5e95010f604f29f628dfc0f596dc3fa0684031b75a43efc481657073daca385", "false"
                )
                .equals("true");
        RESOLVE_REFLECTION_UNKNOWN_ARGS = ZkmUtils.getHashedSystemProperty(
                        "4a4d89b756f2d5f6e05cba65674ff6597f5856d57b2c41d0d847a29a7368c4f0a3c7f8a707166cd4667642bab3541073e6434f8123ce86a9c1f919be8840cdb6", "false"
                )
                .equals("true");
        PARSE_ONLY = ZkmUtils.getHashedSystemProperty(
                        "e57109a154191b161c22e69f02ab56e78af5c7fcba9737b6ecda7c685daf5ac630ea741dd38b565c56a08aaaaf92515476335783bbd6bfa665edfdc01aae53cd", "false"
                )
                .equals("true");
        FOLLOW_MANIFEST_CLASS_PATH = ZkmUtils.getHashedSystemProperty(
                        "d0612a1ad0fad36dddd08084f49f41d279dfc5af129a9b92fcc12281513b1b90adfe1ef68921f5f7631c78c602e42066f66e7a678239bb66b6b753d3e8b8382c", "false"
                )
                .equals("true");
        DONT_ADD_WATERMARK = ZkmUtils.getHashedSystemProperty(
                        "f2ebae5e8608e0086a923841e0a533c5fe5f21abd8d85e596188531305e3bafbc8277d67137de8e422348f1962a0f3e9fd8657131ce3d9779950d5ac14ea2882", "false"
                )
                .equals("true");
        USE_ACCESSED_CLASS_FOR_REFERENCES = ZkmUtils.getHashedSystemProperty(
                        "15df04ac9d900a14fb2c40406a4c31b682c34b6fe580c35d4fd91639c0b2d55b3895958e796ed9ef2b152d4f7a94fbb97fab97bd962566f9dc040ef8b579f2d9", "false"
                )
                .equals("true");
        ALT_REFERENCE_LABEL_PLACEMENT = !ZkmUtils.getHashedSystemProperty(
                        "af745bbfdce8032ba90d45d69819cae6ea103b91a81f0ac6a9d5f977eb0a39872cc21aec855d5f30394f9cb9c5a12458b9b8c710f7d8244132f9ad8ad4ae9a17", ""
                )
                .equals("false");
        ZkmUtils.getHashedSystemProperty(
                "0e9a1a6fca456e7609318846e197e85730463bbdee3271f2c1d4d9e37de5571f856cff639303b6e5b78327d48c9a3abb7123670c36250988e7b9d02ff8944e4d", "false"
        );
        OBFUSCATE_ALL_MEMBER_REFERENCES = ZkmUtils.getHashedSystemProperty(
                        "a50c593868b351feff1aa8c06847cb76d11b9654ae2dcb4015abb0e5221a2ee58f5aeeba55dee118e500471e80557bfa9840a902cfd0cf14d1fcc926c5fd2806", "false"
                )
                .equals("true");
        REFERENCE_OBFUSCATION_METHOD_KEYS = !ZkmUtils.getHashedSystemProperty(
                        "9067176c40d247b4bce0cf36bac8252b5e2a5d533506e474ed982d5ee47c75581a4cb5b794a1b12a6984598d58721c6574f71204ec4851c0c71211f107d2db45", "true"
                )
                .equals("false");
        STACK_MAP_USE_ALL_LOCALS = ZkmUtils.getHashedSystemProperty(
                        "70d795b5e4943631a0d1958b2ee0aa403849e658643341f48306f23f66dc25354e06e9db3e1211e7d15f4eb3dd90e58587134d7c981caddb565eb164541e7841", "false"
                )
                .equals("true");
        KEEP_UNCHANGED_MANIFEST_ENTRIES = ZkmUtils.getHashedSystemProperty(
                        "5f8dd870ecd6174bbb7d383f714e19a79bafefb582ace06dd05da7832133f7eb21a662fa3dafc5c55a44fb019b4a3317840fc45002c861b0db67f5bb943c47db", "false"
                )
                .equals("true");
        TEST_JVM_VERSION = !ZkmUtils.getHashedSystemProperty(
                        "1e61827545955ee0704f99b8a9b8c3c00221604b9021b8ff7cdff05f49c07e98a7602811efcd5a393405d326b6b05dd7700fbca99f4ad19844c55a677f57a650", "true"
                )
                .equals("false");
        DEBUG_REDIRECT_OUT = ZkmUtils.getHashedSystemProperty(
                        "6cb2fbc8c8d490cc840c77be4b73d642f99633d345c9e4218e1bf5ef40531e96696b2303363ce17f92d036c8162c95fd4e58aa04c10412e20dfe3f487deb8686", "false"
                )
                .equals("true");
        AUTO_REFLECTION_OVERRIDE_INDEX = ZkmUtils.getHashedSystemProperty(
                "90b62b2c202c6d8c7e6acee6c8192bc27700016b833fd617243afa327c4d32800206ef638489e5d7c8760ccd878fb3188feca6647d9cb556d0287d02ac6c6e31"
        );
        AUTO_REFLECTION_OVERRIDE_LENGTH = ZkmUtils.getHashedSystemProperty(
                "3ac0aaba6f44ca77fa93325807070f48c2b80023707cccfb7d6f1d2bd7bfccf1ad784caae7effbbc91d609dc5f179dc8af9f031ebffa2583531d9417bfbca771"
        );
        ADJUST_LOCALS = !ZkmUtils.getHashedSystemProperty(
                        "3562dafcc48427b9fbb86328ae202545f4f458c882f0cfbd286c88af37374ec1657d2d17600a181e01a521c3d598ccc5e843e01f117599c3cf529edc254002d9", "true"
                )
                .equals("false");
        ALWAYS_CHECK_RESERVED_CLASS_NAMES = ZkmUtils.getHashedSystemProperty(
                        "55243ef57468b8e4d431ca61f1f2b0fe7059f9cec53d69883813bfcb6dc920a4f7409f4c0f97692d53109a4c9fccfc38db859fe0b84a01b8b35465c92ff530ff", "false"
                )
                .equals("true");
        USE_BOOLEAN_IS_GETTER_TYPE = ZkmUtils.getHashedSystemProperty(
                        "2eeaecb78a2ac8a20c9418a11b0e9108d8dae79aa0558c52be6089d59a7642c3667d08ecd7c78485dba8477a350b4905f9529229e4b43cfdea20d13e480d9ff1", "false"
                )
                .equals("true");
        STRING_ENCRYPT_INTERN = !ZkmUtils.getHashedSystemProperty(
                        "a8362fb7cd090a06043d1a5f71aafac118fc9e02db5527578ab5123bbc26eb0cf42ddbe9e2eb1e35b1772b432bd08a8a28bbf2f586e2facf7e68334474c12e2d", "true"
                )
                .equals("false");
        OBFUSCATE_REFERENCES_INDY = !ZkmUtils.getHashedSystemProperty(
                        "74fc1b18c052b50335d4c60f41277904b96188d2b521e17ebb72a53e21e114220e04b82f278b47496015aa514aea7f9847c47ed5bd5cfdc90302f746ab025e16", "true"
                )
                .equals("false");
        OBFUSCATE_JDK_MEMBER_REFERENCES = !ZkmUtils.getHashedSystemProperty(
                        "0539163c30a633d7a3cc6a7a32f4c2758d4f1a5847bc80c30e496a723ec48ed425a5091569342a2d6c878a341204ab1ec118d04b95d0b050f0edc527b20500ba", "true"
                )
                .equals("false");
        FORCE_FULL_SIZE_FEATURES = ZkmUtils.getHashedSystemProperty(
                        "7feda4444ee618064ba96d7c4098a7f2e4f10799dc91731cd54615fe95be135d0a11c6e2b577d4fa786f0ce6bf1fa94bfed0d156f76af76bad16ad9d10d9e4db", "false"
                )
                .equals("true");
        DISABLE_FULL_SIZE_FEATURES = ZkmUtils.getHashedSystemProperty(
                        "6ebc35e1332b6433dd4f6ddcfc48e1af35d34eba8789cc546ab697773162ed63d8580365db1cfef6bfd462dbc0c02d3276671e26643f3b95960b741fc25f7a36", "false"
                )
                .equals("true");
        NO_STRING_OPAQUE_PREDICATES = ZkmUtils.getHashedSystemProperty(
                        "00b3f66ef0b0a5b00f2ad005f46868bf8b2a2da6b0f29faf0918a0223a70dc9586ddcd808fb7d76bf235d476ccdd95fc08851c1531cb55bad435367848af1356", "false"
                )
                .equals("true");
        TRIM_XML_TEXT = !ZkmUtils.getHashedSystemProperty(
                        "d20d9a061ce6db900d96e26aab06adf76c4f207fa4398d73dab12e9ce5205097a6ca9d6bf577519ed732331fd96b32847bcf9cfe2a83181043cb824baede9139", "true"
                )
                .equals("false");
        COPY_ARCHIVE_DIRECTORIES = !ZkmUtils.getHashedSystemProperty(
                        "7e6d8d53be592654672112f5f40b7ad33ccfbbc59d9923640fd14ac28a33b5fcdca01a0f2349c2f9a557e961a31ff6def01fa7c706f6340b59f00abce3fc9f1c", "true"
                )
                .equals("false");
        TRANSLATE_PROGUARD_UNEXCLUDES = ZkmUtils.getHashedSystemProperty(
                        "c5307e2a70eb81c9783b3f7d2b44712c861cbebeeb19726f108b2fe047883a2b185e15bbd32ff696da1984f54279bee80e7845f7602826deb3f9536d3c73e65a", "false"
                )
                .equals("true");
        SEVEN_BIT_STRING_KEYS = !ZkmUtils.getHashedSystemProperty(
                        "abe695e8820dc8181d36a1d37bc767d4d9d2fa688c4c915b62f8dee8a0157361af4869fadbb6fa679d5deac4d0495f9d65cfa8c08adce2e2c528100f6411f6e4", "true"
                )
                .equals("false");
        FIXED_PARAMETER_KEY = ZkmUtils.getHashedSystemProperty(
                        "a42584d7612eb76092e3bc4493ff3836f08ecfd75174e162fe746e609e236fdd6ad658c55e05066a8bdd3e1e61a28b7137e65cad205d63bf1bbb2c0ffdcd43aa", "false"
                )
                .equals("true");
        CHANGE_LOG_PARAMETER_DETAILS = ZkmUtils.getHashedSystemProperty(
                        "918b3d87c5e0a842149e40818575598f0a08365236aa5954fb052125287351876600f0b3c8245f91635dc9bc556b238844b628157a2de49e3beb8a511de42292", "false"
                )
                .equals("true");
        DEBUG_PARAMETER_CHANGE_LOG = ZkmUtils.getHashedSystemProperty(
                        "c8eaf66ef3c7a27381f9062c26129b5232c65664f5624059a980c511ca2ad2b2c294b663a47801f2fedf8b4b22c53ef489f4bff8a2831ed4f382b24e451ac3c5", "false"
                )
                .equals("true");
        CLASS_INIT_SPEC_FILE = ZkmUtils.getHashedSystemProperty(
                "1116449451721d66a0195b0dcb33cc813f8fdee4f36628258c2d240a0c6a5b960546ef1c541658826941ff062d79260bb33e81d3dd6f60046923c126304e0704"
        );
        RELATIONSHIP_FILE = ZkmUtils.getHashedSystemProperty(
                "e498a3a3ea59e3a0dd5795acf9db529d458a3110188678a62365f8cbfc59a6e85d7f7628f3156340cfc1272e66953a20556ca2cc5901796d29ae3cf6c8700e04"
        );
        FALSE_CLASS_INIT_SPEC_FILE = ZkmUtils.getHashedSystemProperty(
                "da34336ad842b4dcb2afd02ebfb84c9a088a119eb14bddc51431829edafe39368b77891c194303528c8ad5643ae7dd6cee5f4034e391480aa4c3d74e36bd1447"
        );
        STRING_ENCRYPT_DES = !ZkmUtils.getHashedSystemProperty(
                        "4a61d0b8e2107814939e50d5b884abdf39d563d182fce4a2f16db487fd9fd3cd2e8df86947088ec3c91554f902eff2766a5c6de0427a27023555573dea4eb233", "true"
                )
                .equals("false");
        STRING_ENCRYPT_INDY = !ZkmUtils.getHashedSystemProperty(
                        "bd0e178c755d5057f46cbc633bdcb54240d832d25339cbd1405479cc38dc7396d4bc37e07ead3c48269c70f7d2421af3810145d17a13cf6fec2538d75eadd842", "true"
                )
                .equals("false");
        INTEGER_ENCRYPT_DES = !ZkmUtils.getHashedSystemProperty(
                        "140b2d472811d0328200681b57064fa6250edc4652b71c123bb0cb20970bfc4d85be1468860c1b3cd957deb1580db6b28330c8b9b10ef792594d67d79e89405f", "true"
                )
                .equals("false");
        LONG_ENCRYPT_DES = !ZkmUtils.getHashedSystemProperty(
                        "f991be5c9f97d719aba206deb2fb4e8792fa3cd6eb54e88b7a29d772cf0660561ed032a082819cdda0ef79e1671389ea92c2ad4e49c8ca5a15281c8aac05f1f4", "true"
                )
                .equals("false");
        INTEGER_ENCRYPT_INDY = !ZkmUtils.getHashedSystemProperty(
                        "6e68f6e4a4549582af39045e28f103cec79a734f355a86dd503bb730ec4e8b3a4b39b139710d43074eae6455e80e08729498c91653a36ddf886ce9e1f9e0d1db", "true"
                )
                .equals("false");
        LONG_ENCRYPT_INDY = !ZkmUtils.getHashedSystemProperty(
                        "5e54ac98d09de3a6006cff21edb35f430f8cd2cf1f210b936929d28968ece95f1a89c37c59887c7ff4ed6580ef9d1cc5e42ac7fcf8624733b2c2914d7ccad531", "true"
                )
                .equals("false");
        ALLOW_TEMP_FILES_IN_CLASSPATH = !ZkmUtils.getHashedSystemProperty(
                        "f8c66157b1461c904108f630d1abe9d245579a400086c707f829b22e6b2b778cb259706296060e2297066ea311d685c64511bb15917bc9ca81dbdbbb38f52485", "true"
                )
                .equals("false");
        ASSERT_SPRING_BEAN_CLASS = !ZkmUtils.getHashedSystemProperty(
                        "1ba3a0d62aa26981415e5b8efd56ae7b66076537f9e7d3aeecbbdeda5670f4b19885d4632c8b28fa5e9b9e15b7c925c39ae2dde3669c3aa5eeee94336e8d4212", "true"
                )
                .equals("false");
        LAF_NAME = ZkmUtils.getHashedSystemProperty(
                "f0fa985b8bab087abce4916b6a779bff3baffa4d3899e851b50be1ca9d1a61ff1ed169caa4cc853c502ac7771201c040df52a6aeeb90b5432fcaaf3e2f67acf7"
        );
        REBUILD_PARAMETER_MAPS = !ZkmUtils.getHashedSystemProperty(
                        "d3babce40e1cff07d77551c3307de88ed52af815a38b287caa1de63acdd3c0083516887f491843badbb7f62febe7be5df23959d706ea97942e74501965870041", "true"
                )
                .equals("false");
        MATCH_ORIGINAL_NAMES = ZkmUtils.getHashedSystemProperty(
                        "31e372e46eb8a2f01ca90c22cb24e523a215ce7d61343f60e64244ddb92cfef5b80c8b5b062babc1580fe9ee8fb80ac1a55ed4c06cebfcc8c59c421262f6c81d", "false"
                )
                .equals("true");
        METHOD_PARAMETER_MAP_CLASS = ZkmUtils.getHashedSystemProperty(
                "2e185fde20fa27b2b6d1da9d7f9a19c126bb3d8dac279de48b6d10448767dae06af4bf9a33f6fed443e8b3d5c858c066a61af6f8503ca32fa4fc56a2f4409f60"
        );
        USE_PARALLEL = !ZkmUtils.getHashedSystemProperty(
                        "9c7e0172fe226b5c5b23c7152cc7ea407d25ce4b78fd8d175d10853e015b909dce04fdd2b6959477da32b5fd3015955ded17f6df4fd7951dcf4a4684a8e8bf3e", "true"
                )
                .equals("false");
        SYNCHRONIZE_PARAMETER_LOOKUP = ZkmUtils.getHashedSystemProperty(
                        "b8add9cb94552e654522a2a01ff41372cc973dc4f5e1b99a3d46d6252c9d29502938804aa2617bd8990b866a7f3fd25de7cd73572b50a0074a5a6c308878f233", "false"
                )
                .equals("true");
        EXTRA_XML_FILE_TYPES = ZkmUtils.getHashedSystemProperty(
                "f218a38a9cf81839b70a79b7339e655a61fb001cccf7985f5ce8489f34ba2b03086310de9a87d135cf08b1df5a79960fde4da25750d467713b373138356fb024"
        );
        TRANSLATE_KOTLIN = !ZkmUtils.getHashedSystemProperty(
                        "e447e08fd7711fff871f1e72a13c96f52ccae52e242b1dea3227bbd28017da7deafb4aeec0ddab15d4ae419a5b5ca681531ca5de7e1648e3451ebbc3fcfc31be", "true"
                )
                .equals("false");
        MARK_OBFUSCATED_PARAMS_VARARGS = ZkmUtils.getHashedSystemProperty(
                        "0dc26e11372cb56ba3576b3b7e895ca961974f0adedbbb541fdb00fd0e07f7ee3aba128a97f0f0ffd0f56f7e1aeab5881b4e27e15d9a4b550fa7175974de6ab3", "false"
                )
                .equals("true");
        REMOVE_CORRUPT_CLASS = ZkmUtils.getHashedSystemProperty(
                        "bf9328237364c865a68b59db382e9196514da10d2f4bbebef53a5dc7cdf41e330dcd42185b794633801197577a610e533de7308ae86e9f637028f6ca99ecdc1c", "false"
                )
                .equals("true");
        PROCESS_YAML = !ZkmUtils.getHashedSystemProperty(
                        "5f7668e17e634001e8d6c10dcbfa2786b6bff4ef82195fcd0347cfcae4cce081effa3c39f5008e23e32a57a38d12121d49992624fc8d9b101970db224a325c3b", "true"
                )
                .equals("false");
        PROCESS_PROPERTIES = !ZkmUtils.getHashedSystemProperty(
                        "a59376f3080b9cc8b7e470adaca7ee50cb91d110e004c6b2aef80bc60eecc57e061695d2f35a9460021cb1fc7c4b276edd20e615329f86b36443511e3ddc1d1d", "true"
                )
                .equals("false");
        PROPERTIES_ENCODING = ZkmUtils.getHashedSystemProperty(
                "03d2bca23bb3ce9310ee4284010132ebe7c079a29b2c2c99e3d0cd0ba6ed817fa63c1a9e723257313eb1cafee6e4ba2c8286ff4489b32e475cec98cd85914b15"
        );
        DETERMINISTIC_OUTPUT = ZkmUtils.getHashedSystemProperty(
                        "853a2485343d95055264055e358ef9e6e97e935f80a1c5ec0f5cb4d9f8977fa51c790aad44beb946de27c99d8d0916543e1aa54b7a147113e33bcacfaef5a31d", "false"
                )
                .equals("true");
        ENCRYPT_TRIVIAL_INTEGERS = ZkmUtils.getHashedSystemProperty(
                        "b727bdefb22bfd439d89430e1b11793d0a6e6dcea2fac4fe96855037913f401143bb6a2d803c5ad5e056db197e5feaabed9c0c42d93c873e5308ec80c43e5897", "false"
                )
                .equals("true");
        ENCRYPT_TRIVIAL_LONGS = ZkmUtils.getHashedSystemProperty(
                        "8f2a5de3582ae2747d8d82137dd8977c4895dbe621b1e1dfd0611db0ef94f2b410c4518983a35361c7d247e74f4b1db74a225279a152ccec6395110404a6ee57", "false"
                )
                .equals("true");
        skipFrameCompatibilityCheck = !ZkmUtils.getHashedSystemProperty(
                        "b90e12da2adf99f479ecad90a530ea33b8701422fbbe250b5d5f8edb8f7096c8b61dba24a593c4406c81f08cf9f129d95040be9487d1c7f1b0a53713dd37cb2d", "true"
                )
                .equals("false");
        FRAME_CHECK_MODE = ZkmUtils.getHashedSystemProperty(
                "3f1abfe352d1aa2b02e56a0383c1997413b95d38241dee43f0cfd442e8d7d7a625c1e767296d163290d0069ba20661ff932ab39edfc3ebfbd1b15fc7bd071366"
        );
        FOLLOW_LAMBDA_METHOD_HANDLES = !ZkmUtils.getHashedSystemProperty(
                        "f3847e56847cfe1710227d665cd29a661ac65579a2dfa0d35f252baba61d007cb98d630eff63b2e9718d4c4a4e9dc8dd18a4fe81031b52ecc71ac24c45fa0740", "true"
                )
                .equals("false");
        PRESERVE_ZIP_ENTRY_TIMES = ZkmUtils.getHashedSystemProperty(
                        "8c99232262bf3f9fb4ae50d16bad02cf40c59a1c51ca3e3673cd524663ab6a0efc95c7d632be8942119310373a3a6fb390f81874e0140c67113cc3cc95fc3ce7", "false"
                )
                .equals("true");
        NO_RANDOM_INSERTION_POINTS = ZkmUtils.getHashedSystemProperty(
                        "2f163d41b138629c77be62ca4596f612549b83f8f63e8ed914b5471124cea3ac6048144ad8cfad38c1120c6ed5bf261c1f244b952ecf085f48e155b23f1c4617", "false"
                )
                .equals("true");
        INSERT_AT_METHOD_START = ZkmUtils.getHashedSystemProperty(
                        "aff9b87be34bf5166261e13af08363fde4b3c7ba07ac55dd0fb238e4019f615e7902052f776aa594fe126781a3d0ec936702ad7cc7905e3e1d72bb756bcf0d76", "false"
                )
                .equals("true");
        SKIP_EXCEPTION_HANDLER_OBFUSCATION = ZkmUtils.getHashedSystemProperty(
                        "5155078311f8a21deeb48a0036c6d7a736fee65f5ab608afeb7743b4501b73187dd4df87ae8c163b959a62c57e58cc438f5bf9ee39abf340ffc848af7f387666", "false"
                )
                .equals("true");
        SKIP_STRING_CONCAT_ANALYSIS = ZkmUtils.getHashedSystemProperty(
                        "07609958474283d41f037a3e9117604a120374870c119de7c0d5a551bb8495447c44d860f9d76a4d0f38add7b4c9d82c84e8b672c48ab15360fb5a5e45707683", "false"
                )
                .equals("true");
        SKIP_STRING_BUILDER_ANALYSIS = ZkmUtils.getHashedSystemProperty(
                        "21c22120d116b3bb4c0d0b5f8fce8d0a586f81352ab0f0ac4fd34b67803d9e0211482269973bc38549673387f8515f5fa35b44defcf24833459e4a62e481ab5f", "false"
                )
                .equals("true");
        MATCH_UNRESOLVED_SUPERTYPES = ZkmUtils.getHashedSystemProperty(
                        "2e335299b90e925185cd9df93c4c792b2c06a19339615e70b562719a93ec0b4ca67e51cebb49961464edde4c168d301560ddd67cf5baffe46bc75b6962b55a49", "false"
                )
                .equals("true");
        SKIP_SUPERTYPE_MATCHING = ZkmUtils.getHashedSystemProperty(
                        "35cceb8a66ee4ada0b957b379ee0c0807926639348e102738f3ec878c7d5e02aed212280aac67a146a00c248472b05390bbbc8a30c03836926c83772928ab9a3", "false"
                )
                .equals("true");
        CHECK_REPEATED_PARAMETER_CHANGES = ZkmUtils.getHashedSystemProperty(
                        "4d71c9e6edf3930a141a46c2f6949dbd30fb312181896b10038e37c84d5901d8e673ff5ef3a48f75d74f42188e8f3c3c599bd981219c3c71ec019686fcce31b0", "false"
                )
                .equals("true");
        ALWAYS_REGENERATE_PARAM_LAYOUT = ZkmUtils.getHashedSystemProperty(
                        "246be466a92e368372506030668f489bd77dce6dd8d86a5b8936aba3df7fc7236365c66539125390d993bfb70be64006a606e522954086fad447f05a4a2672be", "false"
                )
                .equals("true");
        PREPEND_PROGUARD_CLASSPATH = ZkmUtils.getHashedSystemProperty(
                        "f9ae0d3777e1dfe0d51cb7108805feb9ad94ab536b02cd5aa823c10cfb48b738e486601ae9249322a97b35e91baf4e00d006c620c7542fcd2f0ceced3089fff4", "false"
                )
                .equals("true");
        STRICT_TRY_CATCH_ANALYSIS = ZkmUtils.getHashedSystemProperty(
                        "c9bd2980414955e70c4fe8652fb0b656088434dbbebdee124bb232150400e66234358d53a31a7da4e223b90501f685779ab095b69068373928f457005cf75414", "false"
                )
                .equals("true");
        FORCE_BASIC_SIZE_FEATURES = ZkmUtils.getHashedSystemProperty(
                        "a561945c4a9464698c16e3511ee4be02435408be480df7f2679f8c1b820492c0d40ee46843a4472ac478be129fecb3993748252740ee55f248460fb51d9dbe0e", "false"
                )
                .equals("true");
        DISABLE_BASIC_SIZE_FEATURES = ZkmUtils.getHashedSystemProperty(
                        "64a2fbea7300e21637d41b78c774671bfca3b41c1b5cd68df3ad9728b069a4b9227700a05eb11467a0cf2ec527f39c17ce68ece7686182e913280d7fc9d2c258", "false"
                )
                .equals("true");
        NO_REFERENCE_PARAMETER_KEYS = ZkmUtils.getHashedSystemProperty(
                        "1fbb7ff52ce9431a4f0e32ffaed05b7c00243a12f9618a71e6be911476c06dbb71f1fa412e1175a89e80cb0ae4e1868c330cfa11a11437e668324b93876a0978", "false"
                )
                .equals("true");
        KEEP_UNUSED_PARAMETER_SLOTS = ZkmUtils.getHashedSystemProperty(
                        "8b5a54d1846f3f743eb84d77fc5405d03fec4ad55782c00f49c5abd721f8ed62ff6b4803b9d59687fac8dbd66e3f8fc87d75d2f5eb9aeb5f8b4ad315541f82db", "false"
                )
                .equals("true");
        ALWAYS_TRUE_OPAQUE_PREDICATES = ZkmUtils.getHashedSystemProperty(
                        "278a365d0df9fb144fb2a813b6823ad00d78ffefa0e02dbe76d3af484cee7d16523e8c304499032707cf80868234c8b3657180ac62a3b1d3c4e2465f722a1795", "false"
                )
                .equals("true");
        OBJECT_OPAQUE_PREDICATE_TYPES = ZkmUtils.getHashedSystemProperty(
                        "bfb6fffedad13fdafa2276940ef473995b79575e6a551bb7707d72331e36fc2c58ebfc804fd4c67c4b12c7c2d81ba850f070c6198e15e27f2d6eca1071234f19", "false"
                )
                .equals("true");
        ABORT_ON_PARAMETER_CONFLICT = ZkmUtils.getHashedSystemProperty(
                        "2271cd52d51697bc3feadd2bf264e4f81e949b3d96cf3a3b70c97a957fd92e70afb84de9ac0064fd8295155fa6de4a6e5e9f1621d0cf559aa19432351ebb455c", "false"
                )
                .equals("true");
        SHUFFLE_MEMBERS = ZkmUtils.getHashedSystemProperty(
                        "5cde8881cda1b15ae4e8f5eee965e0506e5da0b6bd527568a2ce6ba47cd9f0ed07b2c3292989796f923fc708323cc7c52f95bbde8c56189e0ff140bab0394c0d", "false"
                )
                .equals("true");
        CHECK_SMALL_CHANGE_SETS = ZkmUtils.getHashedSystemProperty(
                        "3c389453be425689726c0fabc266377cce72838dccb1b33680b3de1a84111699141dce1b126ab266026da9edece30fc9428a84e48c4234bb6f85822b4eb33410", "false"
                )
                .equals("true");
        NO_AUTO_REFLECTION_METHOD_KEYS = ZkmUtils.getHashedSystemProperty(
                        "d2c3aa84f034be3ec979f3c5d325c61584f595d50005ed857f388dadc73f514e68026387e831eb242ba4a17ebff51ec541ff774539267297690a869711abde62", "false"
                )
                .equals("true");
        SIMPLE_PARAMETER_KEYS = ZkmUtils.getHashedSystemProperty(
                        "a13f8c5cf83fa59733b8b5da1c5fa848396918338a1335ce3b1b3d81bfd139119b200227df5d8cbee90d5b30543ff0736e25bea53ddf81c6f2064239da529116", "false"
                )
                .equals("true");
        SKIP_PARAMETER_MAP_REBUILD = ZkmUtils.getHashedSystemProperty(
                        "ad734fcbdac707b79f1a19ce2a421f375b02b2dae70b95808e8dccf8e3fbf5cd8282cafa4cb231a0b048215dd4c42c153192b67e343ced01536dfed4c32fc19e", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "5a7d3e9f4804d5f4619e896281c4c1fffc345bf83e6537da011c40e9f57c959999c55e18ed04896493e45c02b7822c039d4bb47f96c2b8acb8c731eb227af5fe", "false"
        );
        SKIP_HIERARCHY_INIT_ORDER = ZkmUtils.getHashedSystemProperty(
                        "773efd28d903c6d6dbada96f86e9248c025f155e9e497cc4ffa283da6d4a37867dea69f0e70721349adb3021915aa23b9fe6d43865b898e08a0102131ef1c32a", "false"
                )
                .equals("true");
        SKIP_SPECIFIED_INIT_ORDER = ZkmUtils.getHashedSystemProperty(
                        "22d6a20b1d5712b66cb19dea11da39bc8be8d5323769d1b6ee8c4705b1aebba98246f6a1be6ac8b26bf161b809fdfc46847e2327c68236ebadbd176238e0a7d9", "false"
                )
                .equals("true");
        UNLINKED_PARAMETER_CHANGE_NODES = ZkmUtils.getHashedSystemProperty(
                        "bdb7c0f55c94b78455a3a6ff89aec3ce2e23086310529879d05284d7c2e3c74043db6bd578048059b025c07777390ed9ddf7cd7e6305e496d14865e62a208a20", "false"
                )
                .equals("true");
        NO_REFERENCE_METHOD_KEYS = ZkmUtils.getHashedSystemProperty(
                        "cd10a4090f951e29d84120bfab3b309a3dfc756f460cd7c3f3b2b76d9e779264112ce442c3f6b9b20584bff26c1f5c69ff022012d400c27e73b21903eae56970", "false"
                )
                .equals("true");
        NO_METHOD_KEY_FLOW_OBFUSCATION = ZkmUtils.getHashedSystemProperty(
                        "fb08892b1fd749cc3a4584807d512926f37e9f4692a13bd4be09a2c682312df08d1bd194cd1840922aa1acabaebcd915d111af82be9d645d067f038f65957637", "false"
                )
                .equals("true");
        XOR_INDY_OPCODE_KEY = ZkmUtils.getHashedSystemProperty(
                        "2a56675d34e083cc74eff5d3740208e664455182a73957049f1bac73194668994cee762af4c9a525ca5c95d2b1a60cdef3ef8a2579a541425b1be19017b23fdf", "false"
                )
                .equals("true");
        PARAMETER_KEY_SIZE_OVERRIDE = ZkmUtils.getHashedSystemProperty(
                "99478180fee01986c237900c508a44be2e99c225e20888ce6da5a8b497e2bc8507e9f0c6e36a0a989c61cda3d3f4d62b96c76b11d5d9e4fe6afe03af6826264b"
        );
        CHANGE_OVERRIDDEN_METHOD_PARAMETERS = ZkmUtils.getHashedSystemProperty(
                        "3b4828bfd11d7672cae28f829cf1e2dc07ac151fbe576ea9d73f7a4309729e7573e28748c5c0b2cceac252941c804ded5dee7673b46d6fdf850f876b94cae714", "false"
                )
                .equals("true");
        NO_METHOD_HANDLES_LOOKUP = ZkmUtils.getHashedSystemProperty(
                        "4fe8bf83d299a7f0db6adab79ca0a392e51c95a3577781df829ed4a677964795c9712401223241ec80bb2e283f69853eb549abb588d32e3adf3f487312cd9718", "false"
                )
                .equals("true");
        SKIP_UNRELATED_PARAMETER_CHANGES = ZkmUtils.getHashedSystemProperty(
                        "af71831c23086ce5b4d278a32631fd44a5305c7a53b977b1fc2db5f5f556964e275eb01c948ad9e5170e204482d30dadf06e0d000b1ba0f40b60e0fc06258db5", "false"
                )
                .equals("true");
        NO_JAVA7_INDY = ZkmUtils.getHashedSystemProperty(
                        "91916b226afd0cc7e24ed4aba9f3e73c5e8074efc5b974661f4c62dfac185d50f9ac2fadf1d1b7cbce34e7d505ed3f93a884d298d7fda027a449fdd6dca7579c", "false"
                )
                .equals("true");
        ALLOW_HASH_NAMES_IN_NAME_FILE = ZkmUtils.getHashedSystemProperty(
                        "8ce5c351d80bf620317587662c00ecf734a781e6100cb17c514583213040b799c3eec2357bfacca63bbf57ace49870aec3c3070735aee647aa200c874b4470ac", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "9d1520171eea665e42adbb7bd610cb9f3b043dea0eb37b6c144d85654f36447fcc45cf5fe358d508a51fad3671aa31b1d4e5a3fd8f361307bd2781f21911c721", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "6be473c0852406ce6b0b4bafad10606d28f54719f0d17cf4f7c0e4cbb11d02b6f97a1662b3418dc6111271843239db4a83ecdc1f7817992c2b72e4525296111f", "false"
        );
        DONT_SHUFFLE_REFERENCE_OPCODES = ZkmUtils.getHashedSystemProperty(
                        "b5dd22363f45aa5b98a66928b29049cf25fdb18b3ce467c30ccd71ce5ff43d59ecf8da9182b86f43a3671cf6cba35025337b2352cf6b73cf05ed7e1889f1ca38", "false"
                )
                .equals("true");
        SKIP_PARAMETER_LIST_REUSE = ZkmUtils.getHashedSystemProperty(
                        "3dd773367c0cc8fab6d8f6cd3076485696471c820fad2720719330d69f3aad1b26b2ddb6508cbf8290e2ce43cbf173e919a562e1925f822f69629402dd6910da", "false"
                )
                .equals("true");
        STRICT_CLASS_INIT_ORDER = ZkmUtils.getHashedSystemProperty(
                        "282399ead2dadcdd327ffcbb43e2efef0629cf8cb992f7409df19033aad339305573ed332613ce7d55984cce4d5abf52fb4856b37143e2adc13848c330fc5ea8", "false"
                )
                .equals("true");
        RANDOM_CLASS_INIT_LINKS = ZkmUtils.getHashedSystemProperty(
                        "4dc387c1c1585d696438c659ef2e5083b1c59b2adedcf5306bfe52635b06833321e2d05ed1588039188e7cdc7a25255fef679f272001896269dfc45b0a912c54", "false"
                )
                .equals("true");
        NO_CLASS_INIT_LINKS = ZkmUtils.getHashedSystemProperty(
                        "b17764eb5f734592ca51a2f16d83bb28afcce74f5cb7968d15b939d860c41a788af6a545f72cce09fb187f4439ec5e8a2fca254d8adad32d5797c13a1054643c", "false"
                )
                .equals("true");
        EXTRA_PARAMETER_ODDS = ZkmUtils.getHashedSystemProperty(
                "994a1305d7b40e997279c6e57522eba8c17e1801d3995fff79a7940a9b74ab486f279d112ba90350d7c6e580b29c92a6d0f339abe763bce6ad9c2b79a218c885"
        );
        EXTRA_PARAMETER_COUNT = ZkmUtils.getHashedSystemProperty(
                "fc102a8f0a9fc3c71647d2e14ee5999c4eb7606d31969db6278a7d7dfa74741ae48527c23f29f2a2261645ada0ce75e6d2d2a32c98947de723bea9ce85df9e96"
        );
        SKIP_BOOTSTRAP_METHOD_PROCESSING = ZkmUtils.getHashedSystemProperty(
                        "e64f2e57c34f392159095e6ed28ac6509edac31273f5435b3b9eeb535b9bf3a5cd5a1abaf530033367a66b1d4db050b56bf4a686ca713853557d78db64827d0c", "false"
                )
                .equals("true");
        LARGE_PARAMETER_KEYS = ZkmUtils.getHashedSystemProperty(
                        "bf4443b85f1f70e0e8618cd0f84e0c14470052e0d0280628edf262929ec5a978bccda3c3f21e763bccc7d0c27d329936fd4e2f3d4e9ad611b443ee898e1f8b0c", "false"
                )
                .equals("true");
        NO_PARAMETER_KEY_SIZE_LIMIT = ZkmUtils.getHashedSystemProperty(
                        "b8b60918cbaa5caf81df307fe6e0c7997ac215c32ba763500eef9e683af13eb2fbbee98fa4e38a5644353a7c0c4b12dd78c881bf47cf0a421a11a1c1de16fd2f", "false"
                )
                .equals("true");
        RENAME_PARTIAL_DOTTED_STRINGS = ZkmUtils.getHashedSystemProperty(
                        "220e5716a59c09ced8c3f09ea365fdf71af013bf53e74c6a77a1f0a104e095f2d79bbd5b0b0e832a15161123a48b705e68ee92ad3e005ad3a434b6ac8c6159d5", "false"
                )
                .equals("true");
        DONT_RENAME_SLASHED_STRINGS = ZkmUtils.getHashedSystemProperty(
                        "05715874bd5ff52265fe6c96b78bf5caadd45816dcc76c923d9e5e5961dddfe606300d966347e3df1b9a1d101e8322565fa4a1ab6107593e7d93e8fec7bd54be", "false"
                )
                .equals("true");
        DONT_RENAME_DOTTED_STRINGS = ZkmUtils.getHashedSystemProperty(
                        "84a0f76cc5199a19551dea9a830c15b209243ec6134bd520c70e0e39614b09c6a98f3ce915c1e1cfcb20b843a0e109b393b518a7c49f08a42ea290d729d0252e", "false"
                )
                .equals("true");
        TRANSLATE_KOTLIN_METADATA = ZkmUtils.getHashedSystemProperty(
                        "985185e27ed8c496719f4ad43738c62900da7828e968831d958bc1cb130226483661a6d27f53466c722ddeadd480966e9f0e6734a8b1ba8892a4f5ce5a77371d", "false"
                )
                .equals("true");
        LOG_REFERENCE_INCLUSION_MATCHES = ZkmUtils.getHashedSystemProperty(
                        "1d1b979233ffbc9748a34c89e7978e26651815d434c85a75c31c8fa7d5150c77af4cc0216ab7f36fd5dd193c297b4e14ec18db223a39679dbe094419dd9b9118", "false"
                )
                .equals("true");
        DONT_SHUFFLE_CONSTANT_POOL = ZkmUtils.getHashedSystemProperty(
                        "9555750bce6793ea1c683c61087a9b652e16641f3adad684160e1d337a810ce697c57d4d28a390136bc2fda992841241459b2b7229bdd573b31223d385f1181f", "false"
                )
                .equals("true");
        DONT_SHUFFLE_PARAMETERS = ZkmUtils.getHashedSystemProperty(
                        "4cf8f1c175c9df60f5a1feb213ff7b4a264843b1c246dc4bf56e360cce1959fa2cc0fbeb2b37a831a93f4418147b9b4301c943045bc7b27764784edeaec27519", "false"
                )
                .equals("true");
        DONT_SHUFFLE_LOCAL_SLOTS = ZkmUtils.getHashedSystemProperty(
                        "f6a95ae60259beefc27a0d443de8f36ad710dd69972faa873ef25b6c2b979947829ee2024779b50ca558b5acddad368c190725a3c41aa0d1469908d7fc81088e", "false"
                )
                .equals("true");
        LENIENT_TRY_CATCH_ANALYSIS = ZkmUtils.getHashedSystemProperty(
                        "52aa4f663b8797927795af9570477767116b6a40e470ef20ba79b1e0bdee66a8e5d5d662682f762283eaa3444e31d9ecc330ed2a68f7c04ed1cad6b042bfbcc2", "false"
                )
                .equals("true");
        OBFUSCATE_INTERFACE_PARAMETERS = ZkmUtils.getHashedSystemProperty(
                        "4a2f84b6df74ad5e594b609d277a54a73fcf3a3d4a1f46c4788b499cc380ad1d00f0a54c9ddef93ea0fd93fe79cd3ad4737e5761553f29224b0b6dd662c2cd7b", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "d8ea3447df6dcc0d929b94239d43eb7c5f142bd0aa30d7861dc1ada80c9f388b7488a6afc0efb9cf6b2d3c34aa89138d5b5ebcc87fd5d7d982d28ad857ead68d", "false"
        );
        ALWAYS_ADD_EXTRA_PARAMETERS = ZkmUtils.getHashedSystemProperty(
                        "fd24edc204d9a66eab01d90996ce3405c55ea939913d7bc56cff2006529b5ec98b8b0a44b623b6e509636ecbcef10476355857edec54faf91a4e996c7501d43c", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "a0db2b89dd0c02e61713795fa3c78b138fa79e7bb289ff276fb7af4df77fba7b0923e4962895e12052ea70f9d7564a6604977f296c8c63ca6d2d84297b26899a", "false"
        );
        DOT_MESSAGE_SUFFIX = ZkmUtils.getHashedSystemProperty(
                        "c49ea278096489d21009bcd355d748a51642b8f15152c2fbe131f732d9c4e2a7bd81586b5267cee75b193deaa0fc58474ceb791310e1cacaa836f4572f564f4a", "false"
                )
                .equals("true");
        DONT_SHUFFLE_NAME_CHARS = ZkmUtils.getHashedSystemProperty(
                        "ead1536adffbcb844ac97dbedc038b74d80f87cd58d74913acd5386ee9619f34c5c029eeb449569ff0efd6035b1ba1bc0bd8613c644be09ca5b4703c5aedc137", "false"
                )
                .equals("true");
        KEEP_FIELD_ACCESSOR_TYPES = ZkmUtils.getHashedSystemProperty(
                        "6f69d473f4ff6a65f032855748b6f2750f8ab2eef638fd42c2b79c6f386b5668064244520658774571f363ddfd1ce4facd465b11ac2edb41fb8c1a3075183089", "false"
                )
                .equals("true");
        ALT_METHOD_NAME_CHARS = ZkmUtils.getHashedSystemProperty(
                        "9f52dfb2107da33ce1959acc32c1dba5f72dd48dc6634cb2eb619b0508d1c13c3fea4c7fc3f7b652c9ddaacf21cd7825d928ae6331f21d1a61b14b84cf3b8cb5", "false"
                )
                .equals("true");
        ALT_FIELD_NAME_CHARS = ZkmUtils.getHashedSystemProperty(
                        "26b43016732f031e4e520f75fb45862025d12559d5d277204717201acf6404984ec9dec090247bd625a64a608b54534c22868c8d91ee69850ef36d05d04494f7", "false"
                )
                .equals("true");
        SINGLE_REFERENCE_PASS = ZkmUtils.getHashedSystemProperty(
                        "8402c65c1b1177c2715b2aa2f9d226b3bb1aef941f423fcf5389329613727faa6554c5bb875f600aeceda66fb80937dc8d8e7dd5c35b891c1e52cebd27bbca45", "false"
                )
                .equals("true");
        ALWAYS_INLINE_INT_CONSTANTS = ZkmUtils.getHashedSystemProperty(
                        "0780bf5c91372bfc86dac7e6f3fbe503ea9338de0e3f9a9e78f1ecbbe596052346bf2930ac661a135ae1086a710db1822387a6feeeea7746739098bd19625913", "false"
                )
                .equals("true");
        IGNORE_METHOD_SIZE_LIMIT = ZkmUtils.getHashedSystemProperty(
                        "3f0156e9eed9e1c4d84d9edd2f7f3cda3fa2236f016be4bbcb3defcd481b311d1720a03754b0cbdb5230e684530617158df51d318b9b56e051e57d2f1991956d", "false"
                )
                .equals("true");
        USE_PLAIN_RANDOM = ZkmUtils.getHashedSystemProperty(
                        "0b006e2473e6274515751d6345b4e81e6d736224d76a922236208715a841c38a0e5555299dd54f0ac0353608343351553414d18bfa4a1f5187f7fabbee4e2b07", "false"
                )
                .equals("true");
        NO_COLON_SPLIT_XML_NAMES = ZkmUtils.getHashedSystemProperty(
                        "7417f80dba9cff2f6cdbdbdadae00ce5b95e52cb04dfffd3943ef1d4231c35fb689eddd5e5d3e391fa033bd8ea10259df61668b21f9c52bd6a1aadd4f1efbfe9", "false"
                )
                .equals("true");
        KEEP_SOURCE_FILE_NAMES = ZkmUtils.getHashedSystemProperty(
                        "860fc9f771c65de2350b4544ea20118f02c6413a214f81d9dc1725538eb6e43d3a602a03c73c8dd9960179aa70bb314c77f753f2cf92fcb5e67376648a86b765", "false"
                )
                .equals("true");
        FORCE_SKIP_FRAME_CHECK = ZkmUtils.getHashedSystemProperty(
                        "fab2a78f5ccaec523f57347aa6a501ef92c37c5ce78b1b5c099051b55c3a7e1cc45174fce003812e485279b6176d914506d1363eb3ca56128fc867efb5a9f607", "false"
                )
                .equals("true");
        ENFORCE_FRAME_CHECK = ZkmUtils.getHashedSystemProperty(
                        "74f05f336c4afb7a87156697bdbaf511b7cc23515f58d13bfd16b8c2e0a3bc7675da308dc9c6701c55aecde254e080e5d49522beadde368301449a82ef476e46", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "b06c2243919e6bad7a53e9d1d4faff395e1c2bd291548d5bd4a2cc90d1d9cfdf373b08095959dc166d3828525372a5a8b783bbce10061cb76faa515677de63f7", "false"
        );
        LENIENT_INTERFACE_SUPERTYPE = ZkmUtils.getHashedSystemProperty(
                        "1d9510305209f923a9d35ee347fddc65625bf1475c34cb82fd1aa09b6ad3a6e541602537836f841b87be1412aa7a99b2dcc4d009162d83797f6a2a8f835edef2", "false"
                )
                .equals("true");
        NO_VERSION_STAMP = ZkmUtils.getHashedSystemProperty(
                        "2b034b4dd4439101e47ba245dcf0fe1f771b00d7ee376077bbe4161224d1273d6afb79126bfccdf013b39260cb3e244d717f465f74ddb8bc44a20a69421fde32", "false"
                )
                .equals("true");
        NO_TRANSITIVE_INIT_ORDER = ZkmUtils.getHashedSystemProperty(
                        "48d5396fc0ce9eebbf8026e4927bf245b69922ff127b01de02c4af95dc50f19cbe46b5719bab25b4ae8e687718651ce7c51b89596f9e4599d30c50d07e263b4c", "false"
                )
                .equals("true");
        RENAME_FILTER_ERRORS_NOT_FATAL = ZkmUtils.getHashedSystemProperty(
                        "5349271b3282d78a29b6a4020356ba16db4249d6d86509144d6e07b128733bae552e2c45be290eb193d3b71aac0350a57097e18498eb5d761788dcc5d36ed7d8", "false"
                )
                .equals("true");
        SKIP_INHERITED_FIELD_NAMES = ZkmUtils.getHashedSystemProperty(
                        "558ee253d1b4aea579394f921331bb9d276cf6a9dfcabf5c388c56b01836322a34aa4f122d740756f95269886e0ee41b9a4bf824b296567e6a06065372645e46", "false"
                )
                .equals("true");
        SKIP_INTERFACE_FIELD_NAMES = ZkmUtils.getHashedSystemProperty(
                        "80a1dcc36260277a4c5ffafe7de538eb018d9710f5ecd93843031f3214683ce96ef3a7759bfc635dad50a8f8cdbefdf67b15527a14d527c375a9dd9e988b426a", "false"
                )
                .equals("true");
        STOP_AT_FIRST_SPLIT_CANDIDATE = ZkmUtils.getHashedSystemProperty(
                        "2d152e22659f3defc05773211e5e7ec72090fbe5b6252c32637fa2c84ce8b05a456920358d22dc60f0a0a6c80670a769dc829422191f7574a7c86763d8f9d3bc", "false"
                )
                .equals("true");
        STOP_AT_FIRST_BLOCK_SPLIT = ZkmUtils.getHashedSystemProperty(
                        "ec75ee94c6f0562c551ca3fe5615c5426d56ecf674498a7925ece6c323e07c8bbbce6f29b0def330e0af265ea31315a23be4210610d31572d608c84a7bfe6488", "false"
                )
                .equals("true");
        NO_OPAQUE_INSERTION_SPLIT = ZkmUtils.getHashedSystemProperty(
                        "a6dabba7d5742dfe86e78962e15183b42266f3b0023b28640277f8134681d27874bca38cc2b8ac6752de93a9a797715d1013be5936714dcc0d8f320f057962cb", "false"
                )
                .equals("true");
        NO_KEY_INSERTION_SPLIT = ZkmUtils.getHashedSystemProperty(
                        "1fd2c5c5d6cb99b47b34754e81ae8da623e8c082a67349d63afbe0fb47547e3d2eb76eda9c50b8ee2ffd3304eb21482a089429ebc110cf9b0fb2ab8a29c0d24c", "false"
                )
                .equals("true");
        DONT_UNESCAPE_NAME_FILE = ZkmUtils.getHashedSystemProperty(
                        "b0dfa3036aa60f5c6a04fe4bddcf012a542cd30a32e6767507a5bef90e9d749e9d3aeac23ed38180f346180ea459c38812949958b28219fea58093331c4d0076", "false"
                )
                .equals("true");
        VALIDATE_NAME_FILE_IDENTIFIERS = ZkmUtils.getHashedSystemProperty(
                        "8b335c8e6d9ad02396d8ee01a720ac6f7768a4b8b69128ff0de547a2504744e9dbaf9ed63b0f337df668b099acaee999366e829b173c7756bdb30b11ef5879b4", "false"
                )
                .equals("true");
        TRACK_ALL_PARAMETER_USAGE = ZkmUtils.getHashedSystemProperty(
                        "a4109c78057a8f4b33ed0130b4641d8273ae20c28228bceee1890fd523f9abcafe710593a99069ef6f5d4d8154c6b39bd04fcc392acbdd9a978fddfea9c9afd0", "false"
                )
                .equals("true");
        ZkmUtils.getHashedSystemProperty(
                "b4852a3575b21fe4a26a269b65bf99422209391d1b3998a94e053b7649c11c9c0d8db13641de6f5ba097605cf4f105816175dd8dc3af2ad9d490e333a944dae3", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "ef848aeaf8a0b6c84adbdc1a67097954d087ce75091754a41d8d6457bcc694fc27a20f35e967ad4b3289359e860bfd8791e2fbaa6c88a35bced8713fb2966b66", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "48384f369fcab45d778360afa5802d6a1fed5324e2a5f463ca35b941fd7e9789e360cd9177fc1fd56d18144c47d771c10c8990e72f70b78b0021960ac198db0e", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "a1bddaab6ea2c1650d39060054bda325bc4811bea4f7e18af8e2902c553fa9ae4e0d28dd3e4c56e84950dbc1cefab573178255511796873a6578cbd7fb873adb", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "80bfcba74e401722adabd5e0e0cabd0ce429e02663c6665044eb80758f658f60b481683c593e418120a6b1bbb57efc581787790b9eca1eddcf95876007796c2f", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "87835f98443918ff3d6fef88518e12effcc26c4c7133f4fe1167ba57577de24208fe6b381843c6c2569cc6588909159aa3b493bfbf98bbbbdb33b2def0985595", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "780aef1911f0ea4eaf8a31b55faeaed11388e9100360108208c7baaf3ece1b61a3a85e7afda850c30cc575eb953ff84e7433d69c0dd52fecbe6f3aeece9dd5f1", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "e32e9ff3354b800a05c0a1747dee5b29797dd5031c48445dbda09ac3b0d444e310ef77027adffaa5f79f147edf40cf15358471b895c8601ae719c47ced73e98a", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "824261e19f83bc314a35fff1a8c42fdb14afdb4d32af5de92f23b2d7cc16f817db03d7aaa5faba8900c2dfc63796fd638cd5e635995ac3077e719dbfafbf0a2c", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "0cb07f93f3b61d5b2f0337f467b287b3cbeed0756f652ad4080e1bac088f0cd923d1371c23df57ad056fef377da5678ddbe8bea70446c8970bd2994a8ce69b31", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "c10734137de2e615b26b9ec94211bb0655bb2daa7bc3fe6e485d9154f5db7476296a8b32b2411c620b7c981dcff80b2085f938e14e8a885a64547900ba34b0f1", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "a72b0ca7d2ca3692149a678e9e65a8ce03dc3bd8184a234169e858fad021a6e9bf9cb1e34cbc333dc20abf4e4ff66e92a4ab509c6dd966c53f25eef2fdf9fd80", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "05e8618b086086c3a2167ae5f82d66d80be156421c7240669bc10d05d35ed4006ac72eb9ac5ab945977c3048d64cd1d401387bb5b27cd9700a71877fa4ca6dc5", "false"
        );
        ZkmUtils.getHashedSystemProperty(
                "368a6786b705b02a00c725be68d1bd5c2e8d840fb577abf32e27342174583d1f640be634c122438ac3aa1e3db04cc9251b8b5705cec9eca9192fddd1d6117e3c", "false"
        );
    }


    private HiddenOptionFlags() {
    }
}
