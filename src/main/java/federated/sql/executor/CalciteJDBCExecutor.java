/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package federated.sql.executor;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static federated.sql.metadata.LogicSchemaConstants.CONNECTION_URL;
import static federated.sql.metadata.LogicSchemaConstants.DRIVER_NAME;

/**
 * Calcite jdbc executor.
 *
 * This executor used Calcite JDBC driver to execute SQL.
 */
public final class CalciteJDBCExecutor {
    
    private final Properties properties;
    
    private Statement statement;
    
    static {
        try {
            Class.forName(DRIVER_NAME);
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public CalciteJDBCExecutor(final Properties connectionProps) {
        properties = connectionProps;
    }
    
    /**
     * Execute.
     *
     * @return calcite query result
     * @throws SQLException SQL exception
     */
    public ResultSet execute(final String sql, final List<Object> parameters) throws SQLException {
        PreparedStatement statement = DriverManager.getConnection(CONNECTION_URL, properties).prepareStatement(sql);
        setParameters(statement, parameters);
        this.statement = statement;
        return statement.executeQuery();
    }
    
    private void setParameters(final PreparedStatement preparedStatement, final List<Object> parameters) throws SQLException {
        int count = 1;
        for (Object each : parameters) {
            preparedStatement.setObject(count, each);
            count++;
        }
    }
    
    /**
     * Clear resultSet.
     *
     * @throws Exception exception
     */
    public void clearResultSet() throws Exception {
        statement.getConnection().close();
        statement.close();
    }
}
