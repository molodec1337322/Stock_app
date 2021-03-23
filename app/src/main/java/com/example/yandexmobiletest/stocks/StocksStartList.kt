package com.example.yandexmobiletest.stocks

import com.example.yandexmobiletest.stocks.models.StockTickerAndDesc

object StocksStartList {
    val startStockListName = "CACHED_STOCKS"
    var startList: MutableList<StockTickerAndDesc> = mutableListOf(
        StockTickerAndDesc(
            "AAPL",
            "Apple Inc.",
            false
        ),
        StockTickerAndDesc(
            "BKNG",
            "Booking Holdings Inc.",
            false
        ),
        StockTickerAndDesc(
            "MSFT",
            "Microsoft Corporation",
            false
        ),
        StockTickerAndDesc(
            "AVGO",
            "Broadcom Limited",
            false
        ),
        StockTickerAndDesc(
            "AMZN",
            "Amazon.com Inc.",
            false
        ),
        StockTickerAndDesc(
            "ACN",
            "Accenture Plc Class A",
            false
        ),
        StockTickerAndDesc(
            "FB",
            "Facebook Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "WDC",
            "Western Digital Corporation",
            false
        ),
        StockTickerAndDesc(
            "JPM",
            "JPMorgan Chase & Co.",
            false
        ),
        StockTickerAndDesc(
            "GS",
            "Goldman Sachs Group Inc.",
            false
        ),
        StockTickerAndDesc(
            "BRK.B",
            "Berkshire Hathaway Inc. Class B",
            false
        ),
        StockTickerAndDesc(
            "SLB",
            "Schlumberger NV",
            false
        ),
        StockTickerAndDesc(
            "JNJ",
            "Johnson & Johnson",
            false
        ),
        StockTickerAndDesc(
            "CAT",
            "Caterpillar Inc.",
            false
        ),
        StockTickerAndDesc(
            "GOOG",
            "Alphabet Inc. Class C",
            false
        ),
        StockTickerAndDesc(
            "PYPL",
            "PayPal Holdings Inc",
            false
        ),
        StockTickerAndDesc(
            "GOOGL",
            "Alphabet Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "QCOM",
            "QUALCOMM Incorporated",
            false
        ),
        StockTickerAndDesc(
            "XOM",
            "Exxon Mobil Corporation",
            false
        ),
        StockTickerAndDesc(
            "CRM",
            "salesforce.com inc.",
            false
        ),
        StockTickerAndDesc(
            "BAC",
            "Bank of America Corporation",
            false
        ),
        StockTickerAndDesc(
            "NKE",
            "NIKE Inc. Class B",
            false
        ),
        StockTickerAndDesc(
            "WFC",
            "Wells Fargo & Company",
            false
        ),
        StockTickerAndDesc(
            "TMO",
            "Thermo Fisher Scientific Inc.",
            false
        ),
        StockTickerAndDesc(
            "INTC",
            "Intel Corporation",
            false
        ),
        StockTickerAndDesc(
            "USB",
            "U.S. Bancorp",
            false
        ),
        StockTickerAndDesc(
            "T",
            "AT&T Inc.",
            false
        ),
        StockTickerAndDesc(
            "SBUX",
            "Starbucks Corporation",
            false
        ),
        StockTickerAndDesc(
            "V",
            "Visa Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "LMT",
            "Lockheed Martin Corporation",
            false
        ),
        StockTickerAndDesc(
            "CSCO",
            "Cisco Systems Inc.",
            false
        ),
        StockTickerAndDesc(
            "COST",
            "Costco Wholesale Corporation",
            false
        ),
        StockTickerAndDesc(
            "CVX",
            "Chevron Corporation",
            false
        ),
        StockTickerAndDesc(
            "MS",
            "Morgan Stanley",
            false
        ),
        StockTickerAndDesc(
            "UNH",
            "UnitedHealth Group Incorporated",
            false
        ),
        StockTickerAndDesc(
            "PNC",
            "PNC Financial Services Group Inc.",
            false
        ),
        StockTickerAndDesc(
            "PFE",
            "Pfizer Inc.",
            false
        ),
        StockTickerAndDesc(
            "LLY",
            "Eli Lilly and Company",
            false
        ),
        StockTickerAndDesc(
            "HD",
            "Home Depot Inc.",
            false
        ),
        StockTickerAndDesc(
            "UPS",
            "United Parcel Service Inc. Class B",
            false
        ),
        StockTickerAndDesc(
            "PG",
            "Procter & Gamble Company",
            false
        ),
        StockTickerAndDesc(
            "TWX",
            "Time Warner Inc.",
            false
        ),
        StockTickerAndDesc(
            "VZ",
            "Verizon Communications Inc.",
            false
        ),
        StockTickerAndDesc(
            "NEE",
            "NextEra Energy Inc.",
            false
        ),
        StockTickerAndDesc(
            "C",
            "Citigroup Inc.",
            false
        ),
        StockTickerAndDesc(
            "CELG",
            "Celgene Corporation",
            false
        ),
        StockTickerAndDesc(
            "ABBV",
            "AbbVie Inc.",
            false
        ),
        StockTickerAndDesc(
            "LOW",
            "Lowe’s Companies Inc.",
            false
        ),
        StockTickerAndDesc(
            "BA",
            "Boeing Company",
            false
        ),
        StockTickerAndDesc(
            "BLK",
            "BlackRock Inc.",
            false
        ),
        StockTickerAndDesc(
            "KO",
            "Coca-Cola Company",
            false
        ),
        StockTickerAndDesc(
            "CVS",
            "CVS Health Corporation",
            false
        ),
        StockTickerAndDesc(
            "CMCSA",
            "Comcast Corporation Class A",
            false
        ),
        StockTickerAndDesc(
            "AXP",
            "American Express Company",
            false
        ),
        StockTickerAndDesc(
            "MA",
            "Mastercard Incorporated Class A",
            false
        ),
        StockTickerAndDesc(
            "MU",
            "Micron Technology Inc.",
            false
        ),
        StockTickerAndDesc(
            "PM",
            "Philip Morris International Inc.",
            false
        ),
        StockTickerAndDesc(
            "CHTR",
            "Charter Communications Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "PEP",
            "PepsiCo Inc.",
            false
        ),
        StockTickerAndDesc(
            "SCHW",
            "Charles Schwab Corporation",
            false
        ),
        StockTickerAndDesc(
            "ORCL",
            "Oracle Corporation",
            false
        ),
        StockTickerAndDesc(
            "MDLZ",
            "Mondelez International Inc. Class A",
            false
        ),
        StockTickerAndDesc(
            "DIS",
            "Walt Disney Company",
            false
        ),
        StockTickerAndDesc(
            "CB",
            "Chubb Limited",
            false
        ),
        StockTickerAndDesc(
            "MRK",
            "Merck & Co. Inc.",
            false
        ),
        StockTickerAndDesc(
            "COP",
            "ConocoPhillips",
            false
        ),
        StockTickerAndDesc(
            "NVDA",
            "NVIDIA Corporation",
            false
        ),
        StockTickerAndDesc(
            "AMAT",
            "Applied Materials Inc.",
            false
        ),
        StockTickerAndDesc(
            "MMM",
            "3M Company",
            false
        ),
        StockTickerAndDesc(
            "DHR",
            "Danaher Corporation",
            false
        ),
        StockTickerAndDesc(
            "AMGN",
            "Amgen Inc.",
            false
        ),
        StockTickerAndDesc(
            "HLT",
            "Hilton Worldwide Holdings Inc",
            false
        ),
        StockTickerAndDesc(
            "IBM",
            "International Business Machines Corporation",
            false
        ),
        StockTickerAndDesc(
            "AMT",
            "American Tower Corporation",
            false
        ),
        StockTickerAndDesc(
            "NFLX",
            "Netflix Inc.",
            false
        ),
        StockTickerAndDesc(
            "CL",
            "Colgate-Palmolive Company",
            false
        ),
        StockTickerAndDesc(
            "WMT",
            "Walmart Inc.",
            false
        ),
        StockTickerAndDesc(
            "GD",
            "General Dynamics Corporation",
            false
        ),
        StockTickerAndDesc(
            "MO",
            "Altria Group Inc",
            false
        ),
        StockTickerAndDesc(
            "FDX",
            "FedEx Corporation",
            false
        ),
        StockTickerAndDesc(
            "MCD",
            "McDonald’s Corporation",
            false
        ),
        StockTickerAndDesc(
            "RTN",
            "Raytheon Company",
            false
        ),
        StockTickerAndDesc(
            "GE",
            "General Electric Company",
            false
        ),
        StockTickerAndDesc(
            "WBA",
            "Walgreens Boots Alliance Inc",
            false
        ),
        StockTickerAndDesc(
            "HON",
            "Honeywell International Inc.",
            false
        ),
        StockTickerAndDesc(
            "NOC",
            "Northrop Grumman Corporation",
            false
        ),
        StockTickerAndDesc(
            "MDT",
            "Medtronic plc",
            false
        ),
        StockTickerAndDesc(
            "BIIB",
            "Biogen Inc.",
            false
        ),
        StockTickerAndDesc(
            "ABT",
            "Abbott Laboratories",
            false
        ),
        StockTickerAndDesc(
            "BDX",
            "Becton Dickinson and Company",
            false
        ),
        StockTickerAndDesc(
            "TXN",
            "Texas Instruments Incorporated",
            false
        ),
        StockTickerAndDesc(
            "ANTM",
            "Anthem Inc.",
            false
        ),
        StockTickerAndDesc(
            "BMY",
            "Bristol-Myers Squibb Company",
            false
        ),
        StockTickerAndDesc(
            "AET",
            "Aetna Inc.",
            false
        ),
        StockTickerAndDesc(
            "ADBE",
            "Adobe Systems Incorporated",
            false
        ),
        StockTickerAndDesc(
            "EOG",
            "EOG Resources Inc.",
            false
        ),
        StockTickerAndDesc(
            "UNP",
            "Union Pacific Corporation",
            false
        ),
        StockTickerAndDesc(
            "BK",
            "Bank of New York Mellon Corporation",
            false
        ),
        StockTickerAndDesc(
            "GILD",
            "Gilead Sciences Inc.",
            false
        ),
        StockTickerAndDesc(
            "ATVI",
            "Activision Blizzard Inc.",
            false
        )
    )
}