package ru.netology.web.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static DataHelper.VerificationCode getVerificationCode(DataHelper.AuthInfo authInfo) {
        var codeSQL = "SELECT code FROM auth_codes "
                + "WHERE user_id = (SELECT id FROM users WHERE login = ?) "
                + "ORDER BY created DESC LIMIT 1";
        try (var conn = getConn()) {
            var code = QUERY_RUNNER.query(conn, codeSQL, new ScalarHandler<String>(), authInfo.getLogin());
            return new DataHelper.VerificationCode(code);
        }
    }

    @SneakyThrows
    public static String getUserStatus(String login) {
        var statusSQL = "SELECT status FROM users WHERE login = ?";
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, statusSQL, new ScalarHandler<String>(), login);
        }
    }

    @SneakyThrows
    public static void cleanAuthCodes() {
        try (var conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
        }
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM auth_codes");
            QUERY_RUNNER.execute(conn, "DELETE FROM card_transactions");
            QUERY_RUNNER.execute(conn, "DELETE FROM cards");
            QUERY_RUNNER.execute(conn, "DELETE FROM users");
        }
    }
}