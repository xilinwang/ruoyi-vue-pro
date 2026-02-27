package cn.iocoder.yudao.module.studybuddy.framework.mybatis.type;

import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL jsonb 类型处理器
 *
 * 用于将 String 类型的 JSON 字符串与 PostgreSQL 的 jsonb 类型进行转换
 *
 * @author StudyBuddy
 */
@MappedTypes({String.class})
public class PostgresJsonbTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 将 JSON 字符串转换为 PostgreSQL jsonb 格式
        pgobjectSet(ps, i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonbString = rs.getString(columnName);
        return jsonbString;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonbString = rs.getString(columnIndex);
        return jsonbString;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonbString = cs.getString(columnIndex);
        return jsonbString;
    }

    /**
     * 使用 PGobject 设置 jsonb 参数
     */
    private void pgobjectSet(PreparedStatement ps, int i, String parameter) throws SQLException {
        try {
            // 创建 PGobject 并设置类型为 jsonb
            Object pgObject = Class.forName("org.postgresql.util.PGobject").newInstance();
            pgObject.getClass().getMethod("setType", String.class).invoke(pgObject, "jsonb");
            pgObject.getClass().getMethod("setValue", String.class).invoke(pgObject, parameter);
            ps.setObject(i, pgObject);
        } catch (Exception e) {
            // 如果 PGobject 不可用，直接使用字符串（PostgreSQL 可能会自动转换）
            ps.setString(i, parameter);
        }
    }
}
