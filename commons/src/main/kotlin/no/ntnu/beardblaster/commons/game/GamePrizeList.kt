package no.ntnu.beardblaster.commons.game

class GamePrizeList {

    /**
     * Function to fetch random prizes from the winner loot table
     * @param amount amount of random prizes to fetch
     */
    fun getWinnerPrizes(amount: Int): List<Loot> {
        if (amount <= 1) return listOf(winnerPrizes.random())
        if (amount >= winnerPrizes.size) return winnerPrizes

        val itemList = ArrayList<String>()
        val returnedPrizes = ArrayList<Loot>()

        for (iterations in 0..amount) {
            if(itemList.isEmpty()) {
                val nextPrize = winnerPrizes.random()
                returnedPrizes.add(nextPrize)
                itemList.add(nextPrize.item)
            } else {
                val nextPrize = winnerPrizes.filter { aPrice -> !itemList.contains(aPrice.item) }.random()
                returnedPrizes.add(nextPrize)
                itemList.add(nextPrize.item)
            }
         }

        return returnedPrizes;
    }

    /**
     * Function to fetch random prizes from the looser loot table
     * @param amount amount of random prizes to fetch
     */
    fun getLooserPrizes(amount: Int): List<Loot> {
        if (amount <= 1) return listOf(loosingPrizes.random())
        if (amount >= loosingPrizes.size) return loosingPrizes

        val itemList = ArrayList<String>()
        val returnedPrizes = ArrayList<Loot>()

        for (iterations in 0..amount) {
            if(itemList.isEmpty()) {
                val nextPrize = loosingPrizes.random()
                returnedPrizes.add(nextPrize)
                itemList.add(nextPrize.item)
            } else {
                val nextPrize = loosingPrizes.filter { aPrice -> !itemList.contains(aPrice.item) }.random()
                returnedPrizes.add(nextPrize)
                itemList.add(nextPrize.item)
            }
        }

        return returnedPrizes;
    }

    companion object {
        val winnerPrizes = listOf(
            Loot("Glory", 1),
            Loot("King's Invitation", 1),
            Loot("An ancient spell book", 1),
            Loot("Golden Coins", 50),
            Loot("Beard Oil - Magical Quality", 1),
            Loot("Beard Oil - Royal Quality", 1),
            Loot("The Perfect Robe", 1),
            Loot("Gandalf's Magical Tobacco", 1)
        )
        val loosingPrizes = listOf(
            Loot("Wool sock. 'Love, Mom' is imprinted inside", 1),
            Loot("King's Invitation - from last year", 1),
            Loot("Scrap Metal", 1),
            Loot("Irritating Goblin Maid", 1),
            Loot("Rocks", 3),
            Loot("A 'Just-too-short' Robe", 1),
            Loot("Expired Coins", 21),
            Loot("Terrible Boots", 2)
        )
    }
}
