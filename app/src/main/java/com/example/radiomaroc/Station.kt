package com.example.radiomaroc

data class Station(
    val id: Int,
    val name: String,
    val streamUrl: String,
    val category: String = "عام"
)

object StationRepository {

    val stations = listOf(
        Station(1, "Medi1 Radio", "https://medi1.ice.infomaniak.ch/medi1-128.mp3", "أخبار وعام"),
        Station(2, "Radio 2M", "https://2m.ice.infomaniak.ch/2m-128.mp3", "أخبار وعام"),
        Station(3, "Hit Radio", "https://hitradio.ice.infomaniak.ch/hitradio-128.mp3", "موسيقى"),
        Station(4, "Chada FM", "https://chadafm.ice.infomaniak.ch/chadafm-128.mp3", "موسيقى"),
        Station(5, "Cap Radio", "https://capradio.ice.infomaniak.ch/capradio-128.mp3", "موسيقى"),
        Station(6, "Aswat Radio", "https://aswat.ice.infomaniak.ch/aswat-128.mp3", "منوعات"),
        Station(7, "MFM Radio", "https://mfm.ice.infomaniak.ch/mfm-128.mp3", "موسيقى"),
        Station(8, "Atlantic Radio", "https://atlantic.ice.infomaniak.ch/atlantic-128.mp3", "موسيقى"),
        Station(9, "Radio Plus", "https://radioplus.ice.infomaniak.ch/radioplus-128.mp3", "منوعات"),
        Station(10, "Radio Mars", "https://radiomars.ice.infomaniak.ch/radiomars-128.mp3", "موسيقى")
    )
}
