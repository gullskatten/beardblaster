package no.ntnu.beardblaster.commons.game

class GamePrizeList {

    /**
     * Function to fetch random prizes from the winner loot table
     * @param amount amount of random prizes to fetch
     */
    fun getWinnerPrizes(amount: Int, beardLengthIncrease: Int): List<Loot> {
        if (amount <= 1) return mutableListOf(winnerPrizes.random()).plus(Loot("Beard Length", beardLengthIncrease))
        if (amount >= winnerPrizes.size)  return winnerPrizes.plus(Loot("Beard Length", beardLengthIncrease))

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

        return returnedPrizes.plus(Loot("Beard Length", beardLengthIncrease))
    }

    /**
     * Function to fetch random prizes from the loser loot table
     * @param amount amount of random prizes to fetch
     */
    fun getLoserPrizes(amount: Int, beardLengthIncrease: Int): List<Loot> {
        if (amount <= 1) return mutableListOf(losingPrizes.random()).plus(Loot("Beard Length", beardLengthIncrease))
        if (amount >= losingPrizes.size)  return losingPrizes.plus(Loot("Beard Length", beardLengthIncrease))

        val itemList = ArrayList<String>()
        val returnedPrizes = ArrayList<Loot>()

        for (iterations in 0..amount) {
            if(itemList.isEmpty()) {
                val nextPrize = losingPrizes.random()
                returnedPrizes.add(nextPrize)
                itemList.add(nextPrize.item)
            } else {
                val nextPrize = losingPrizes.filter { aPrice -> !itemList.contains(aPrice.item) }.random()
                returnedPrizes.add(nextPrize)
                itemList.add(nextPrize.item)
            }
        }
        return returnedPrizes.plus(Loot("Beard Length", beardLengthIncrease))
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
        val losingPrizes = listOf(
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
