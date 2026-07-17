package com.medo.api.tenant;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

    @Autowired
    private DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection c) throws SQLException { c.close(); }

    @Override
    public Connection getConnection(String tenantId) throws SQLException {
        Connection c = getAnyConnection();
        setSearchPath(c, tenantId);
        return c;
    }

    @Override
    public void releaseConnection(String tenantId, Connection c) throws SQLException {
        setSearchPath(c, "public");
        releaseAnyConnection(c);
    }

    @Override public boolean supportsAggressiveRelease() { return false; }
    @Override public boolean isUnwrappableAs(Class<?> t)  { return false; }
    @Override public <T> T unwrap(Class<T> t)             { throw new UnsupportedOperationException(); }

    private void setSearchPath(Connection c, String schema) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("SET search_path TO " + schema + ", public");
        }
    }
}
