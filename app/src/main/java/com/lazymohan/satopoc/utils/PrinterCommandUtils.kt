package com.lazymohan.satopoc.utils

object PrinterCommandUtils {

    fun getCalibrationTemplate(): String {
        return "CT~~CD,~CC^~CT~\n" +
                "^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR3,3~SD20^JUS^LRN^CI0^XZ\n" +
                "^XA\n" +
                "^MMT\n" +
                "^PW1417\n" +
                "^LL0945\n" +
                "^LS0\n" +
                "^FO0,448^GFA,11264,11264,00176,:Z64:\n" +
                "eJzt0jEKgDAQRNGIhaX3v6WlhUSt0wQXlkR47wSfYUoBAAAAAF7L3agja9a2pi+xd/tek9ibUhPu3cfU9NU/5M0y5xU9Y0rvGa1J6U2sCfQeI2sAAAAAYCYPEj5PIg==:DDE6\n" +
                "^FO928,448^GFA,03840,03840,00008,:Z64:\n" +
                "eJztxTENACAMALARjvl3hBQkIAEPHCOB9mnEs3LYvnWftm3/c1u2qz+yARGFhVw=:1430\n" +
                "^FO0,96^GFA,06144,06144,00024,:Z64:\n" +
                "eJzt10EOgyAQBVAMC5ccgaN4NDyaR6E36NJF018RUUjdwAyppsymzYsx6cwwTIVo0eLO0c08DrB4D2Cq7BjpLpw/mfzd/JLOUF+ufuNyU9clzs9drutFz6qS6/vb5VzD1e4aluBm97QZM/3IpknSmuva+VzNOwSXyeErc2zvL3d1+PLVsrvxvk4HRP1DcBUfmkzvPa8JMGR/uMJKVPDBZ9N/UH0OP6PYffeMftLX8tf+AN2FSPKZ62s325u4CP4Sv3bHYRHQ/+VdPPUK3VZ2N8VU/OejzHvXgIbHJejuLooh2UA5HdtdHm98RW6TcVPkaoMBSHYoVvf3iKv9QPQ1rTqqWK53SOOqHi7ey3jYf3x871FkV7FP5d6iRYtbxgcGVgW8:BB51\n" +
                "^FT71,410^A0N,62,62^FH\\^FDInst.:^FS\n" +
                "^FT353,406^A0N,58,36^FH\\^FDCALIBRATION OF TEMPRATURE MESUREMENTLOOP^FS\n" +
                "^FT61,579^A0N,62,62^FH\\^FDCal Dt :^FS\n" +
                "^FT333,579^A0N,62,60^FH\\^FDDD.MM.YYYY^FS\n" +
                "^FT735,726^A0N,62,62^FH\\^FDDue Dt :^FS\n" +
                "^FT973,726^A0N,62,62^FH\\^FDDD.MM.YYYY^FS\n" +
                "^FT61,726^A0N,62,62^FH\\^FDAsset ID :^FS\n" +
                "^FT333,726^A0N,62,62^FH\\^FDMXXXXX^FS\n" +
                "^FT725,579^A0N,62,62^FH\\^FDWO No:^FS\n" +
                "^FT973,579^A0N,62,62^FH\\^FDMW0XXXXXX^FS\n" +
                "^FT73,868^A0N,62,62^FH\\^FDCal By:^FS\n" +
                "^FT499,868^A0N,62,62^FH\\^FD000098765^FS\n" +
                "^FT1103,916^BQN,2,6\n" +
                "^FH\\^FDLA,123456789012^FS\n" +
                "^FO20,335^GB1363,586,5^FS\n" +
                "^FO20,624^GB1363,0,5^FS\n" +
                "^FO20,747^GB1363,0,5^FS\n" +
                "^FO708,475^GB0,279,5^FS\n" +
                "^FO325,342^GB0,580,5^FS\n" +
                "^FT204,298^A0N,67,67^FH\\^FDCALIBRATION OF CRITICAL INSTRUMENTS^FS\n" +
                "^FT175,197^A0N,75,74^FH\\^FDSERUM INSTITUTE OF INDIA PVT. LTD.^FS\n" +
                "^PQ1,0,1,Y^XZ\n"
    }

    fun getTemp() : String {
        return "^XA\n" +
                "\n" +
                "^FX Top section with logo, name and address.\n" +
                "^CF0,60\n" +
                "^FO50,50^GB100,100,100^FS\n" +
                "^FO75,75^FR^GB100,100,100^FS\n" +
                "^FO93,93^GB40,40,40^FS\n" +
                "^FO220,50^FDIntershipping, Inc.^FS\n" +
                "^CF0,30\n" +
                "^FO220,115^FD1000 Shipping Lane^FS\n" +
                "^FO220,155^FDShelbyville TN 38102^FS\n" +
                "^FO220,195^FDUnited States (USA)^FS\n" +
                "^FO50,250^GB700,3,3^FS\n" +
                "\n" +
                "^FX Second section with recipient address and permit information.\n" +
                "^CFA,30\n" +
                "^FO50,300^FDJohn Doe^FS\n" +
                "^FO50,340^FD100 Main Street^FS\n" +
                "^FO50,380^FDSpringfield TN 39021^FS\n" +
                "^FO50,420^FDUnited States (USA)^FS\n" +
                "^CFA,15\n" +
                "^FO600,300^GB150,150,3^FS\n" +
                "^FO638,340^FDPermit^FS\n" +
                "^FO638,390^FD123456^FS\n" +
                "^FO50,500^GB700,3,3^FS\n" +
                "\n" +
                "^FX Third section with bar code.\n" +
                "^BY5,2,270\n" +
                "^FO100,550^BC^FD12345678^FS\n" +
                "\n" +
                "^FX Fourth section (the two boxes on the bottom).\n" +
                "^FO50,900^GB700,250,3^FS\n" +
                "^FO400,900^GB3,250,3^FS\n" +
                "^CF0,40\n" +
                "^FO100,960^FDCtr. X34B-1^FS\n" +
                "^FO100,1010^FDREF1 F00B47^FS\n" +
                "^FO100,1060^FDREF2 BL4H8^FS\n" +
                "^CF0,190\n" +
                "^FO470,955^FDCA^FS\n" +
                "\n" +
                "^XZ"
    }

}