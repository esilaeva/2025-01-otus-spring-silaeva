package ru.otus.hw.testdata;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.utils.TestUtils;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "esilaeva", runAlways = true)
    public void dropDb(MongoDatabase database) {

        database.drop();
    }

    @ChangeSet(order = "002", id = "insert Authors", author = "esilaeva", runAlways = true)
    public void insertAuthors(MongockTemplate mongockTemplate) {

        mongockTemplate.insertAll(TestUtils.authors);
    }

    @ChangeSet(order = "003", id = "insert Genres", author = "esilaeva", runAlways = true)
    public void insertGenres(MongockTemplate mongockTemplate) {

        mongockTemplate.insertAll(TestUtils.genres);
    }

    @ChangeSet(order = "004", id = "insert Books", author = "esilaeva", runAlways = true)
    public void insertBooks(MongockTemplate mongockTemplate) {

        mongockTemplate.insertAll(TestUtils.books);
    }

    @ChangeSet(order = "005", id = "insert Comments", author = "esilaeva", runAlways = true)
    public void insertComments(MongockTemplate mongockTemplate) {

        mongockTemplate.insertAll(TestUtils.comments);
    }
}
