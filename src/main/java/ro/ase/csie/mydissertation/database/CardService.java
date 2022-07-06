package ro.ase.csie.mydissertation.database;

import java.util.List;

import ro.ase.csie.mydissertation.models.ByteAccount;
import ro.ase.csie.mydissertation.models.ByteCard;

public class CardService implements CardDao{

    private CardDao tableInstance;

    public CardService(CardDao tableInstance) {
        this.tableInstance = tableInstance;
    }

    @Override
    public List<ByteCard> getCards() {
        return tableInstance.getCards();
    }

    @Override
    public List<ByteCard> getCardsFromFolder(int folderID) {
        return tableInstance.getCardsFromFolder(folderID);
    }

    @Override
    public ByteCard getCard(int cardID) {
        return tableInstance.getCard(cardID);
    }

    @Override
    public List<ByteCard> filterByHostOrName(String text) {
        return tableInstance.filterByHostOrName(text);
    }

    @Override
    public void insertCard(ByteCard card) {
        tableInstance.insertCard(card);
    }

    @Override
    public void updateCard(ByteCard card) {
        tableInstance.updateCard(card);
    }

    @Override
    public void deleteCard(ByteCard card) {
        tableInstance.deleteCard(card);
    }
}
