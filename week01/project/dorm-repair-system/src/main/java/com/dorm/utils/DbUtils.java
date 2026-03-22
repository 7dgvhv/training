package com.dorm.utils;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.dorm.mapper.RepairOrderMapper;
import com.dorm.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;

public class DbUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 加载数据库配置文件
            Properties props = new Properties();
            InputStream input = Resources.getResourceAsStream("db.properties");
            props.load(input);

            // 创建数据源 (MyBatis 自带 UnpooledDataSource)
            org.apache.ibatis.datasource.unpooled.UnpooledDataSource dataSource = new org.apache.ibatis.datasource.unpooled.UnpooledDataSource();
            dataSource.setDriver(props.getProperty("jdbc.driver"));
            dataSource.setUrl(props.getProperty("jdbc.url"));
            dataSource.setUsername(props.getProperty("jdbc.username"));
            dataSource.setPassword(props.getProperty("jdbc.password"));

            // MyBatis-Plus 配置
            MybatisConfiguration configuration = new MybatisConfiguration();
            configuration.setEnvironment(new org.apache.ibatis.mapping.Environment("development",
                    new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory(), dataSource));
            configuration.addMapper(UserMapper.class);
            configuration.addMapper(RepairOrderMapper.class);

            // 构建 SqlSessionFactory
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        } catch (IOException e) {
            throw new RuntimeException("初始化数据库连接失败", e);
        }
    }

    /**
     * 执行数据库操作，自动管理 session
     * @param func 需要执行的数据库操作，参数为 SqlSession，返回结果
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public static <T> T execute(Function<SqlSession, T> func) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) { // true 自动提交事务
            return func.apply(session);
        }
    }
}