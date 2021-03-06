package org.smarty.core.config.condition;

import org.smarty.core.support.jdbc.support.DataSourceType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * JdbcDataSource
 */
public final class JdbcDataSource implements Condition {

	public JdbcDataSource() {
	}

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment env = context.getEnvironment();
		DataSourceType dst = env.getProperty("default.dataSource", DataSourceType.class);
		return DataSourceType.JDBC == dst;
	}
}