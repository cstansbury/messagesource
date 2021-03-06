package org.synyx.messagesource.jdbc;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;


public class JdbcMessageProviderUnitTest {

    private JdbcTemplate template;
    private JdbcMessageProvider provider;


    @Before
    public void before() throws ClassNotFoundException, SQLException {

        Class.forName("org.h2.Driver");

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:~/test");

        template = new JdbcTemplate(ds);

        template.execute("DROP Table Message IF EXISTS");
        template.execute("CREATE TABLE `Message` (" + "`basename` VARCHAR( 31 ) NOT NULL ,"
                + "`language` VARCHAR( 7 ) NULL ," + "`country` VARCHAR( 7 ) NULL ," + "`variant` VARCHAR( 7 ) NULL ,"
                + "`key` VARCHAR( 255 ) NULL ," + "`message` TEXT NULL" + ")");

        provider = new JdbcMessageProvider();
        provider.setDataSource(ds);

    }


    @Test
    public void testReturnsDefaultMessage() {

        TestUtils.insertMessage(template, "foo", "bar");
        TestUtils.assertMessage(provider, null, null, null, "foo", "bar");

    }


    @Test
    public void testReturnsLanguage() {

        TestUtils.insertMessage(template, "de", "foo", "bar");
        TestUtils.assertMessage(provider, "de", null, null, "foo", "bar");

    }


    @Test
    public void testReturnsLanguageCountry() {

        TestUtils.insertMessage(template, "de", "DE", "foo", "bar");
        TestUtils.assertMessage(provider, "de", "DE", null, "foo", "bar");

    }


    @Test
    public void testMoreMessages() {

        TestUtils.insertMessage(template, "de", "DE", "foo", "bar");
        TestUtils.insertMessage(template, "de", "DE", "key", "value");
        TestUtils.insertMessage(template, "de", "foo2", "bar2");
        TestUtils.insertMessage(template, "de", "key2", "value2");
        TestUtils.insertMessage(template, "x", "y");

        TestUtils.assertMessage(provider, "de", "DE", null, "foo", "bar");
        TestUtils.assertMessage(provider, "de", "DE", null, "key", "value");
        TestUtils.assertMessage(provider, "de", null, null, "foo2", "bar2");
        TestUtils.assertMessage(provider, "de", null, null, "key2", "value2");
        TestUtils.assertMessage(provider, null, null, null, "x", "y");

    }


    @Test
    public void testReturnsLanguageCountryVariant() {

        TestUtils.insertMessage(template, "de", "DE", "X", "foo", "bar");
        TestUtils.assertMessage(provider, "de", "DE", "X", "foo", "bar");

    }


    @After
    public void after() {

        template.execute("DROP Table Message IF EXISTS");
    }


    @Test
    public void testSetup() {

        TestUtils.insertMessage(template, "key", "message");
        TestUtils.insertMessage(template, "de", "key", "message");
        TestUtils.insertMessage(template, "de", "de", "key", "message");
        TestUtils.insertMessage(template, "de", "de", "POSIX", "key", "message");

    }
}
