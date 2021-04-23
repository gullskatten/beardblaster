package no.ntnu.beardblaster.commons.game

class GamePrizeList {

    /**
     * Function to fetch random prizes from the winner loot table
     * @param amount amount of random prizes to fetch
     */
    fun getWinnerPrizes(amount: Int): List<Prize> {
        if (amount <= 1) return listOf(winnerPrizes.random())
        if (amount >= winnerPrizes.size) return winnerPrizes

        val itemList = ArrayList<String>()
        val returnedPrizes = ArrayList<Prize>()

        for (iterations in 0..amount) {
            val nextPrize =
                winnerPrizes.filter { aPrice -> itemList.contains(aPrice.item) }.random()
            returnedPrizes.add(nextPrize)
        }

        return returnedPrizes;
    }

    /**
     * Function to fetch random prizes from the looser loot table
     * @param amount amount of random prizes to fetch
     */
    fun getLooserPrizes(amount: Int): List<Prize> {
        if (amount <= 1) return listOf(winnerPrizes.random())
        if (amount >= winnerPrizes.size) return winnerPrizes

        val itemList = ArrayList<String>()
        val returnedPrizes = ArrayList<Prize>()

        for (iterations in 0..amount) {
            val nextPrize =
                loosingPrizes.filter { aPrice -> itemList.contains(aPrice.item) }.random()
            returnedPrizes.add(nextPrize)
        }

        return returnedPrizes;
    }

    companion object {
        val winnerPrizes = listOf(
            Prize("Glory", 1),
            Prize("King's Invitation", 1),
            Prize("An ancient spell book", 1),
            Prize("Golden Coins", 50),
            Prize("Beard Oil - Magical Quality", 1),
            Prize("Beard Oil - Royal Quality", 1),
            Prize("The Perfect Robe", 1),
            Prize("Gandalf's Magical Tobacco", 1)
        )
        val loosingPrizes = listOf(
            Prize("Wool sock. 'Love, Mom' is imprinted inside", 1),
            Prize("King's Invitation - from last year", 3),
            Prize("Scrap Metal", 1),
            Prize("Irritating Goblin Maid", 1),
            Prize("Rocks", 3),
            Prize("A 'Just-too-short' Robe", 1),
            Prize("Expired Coins", 21),
            Prize("Terrible Boots", 2)
        )
    }
}
