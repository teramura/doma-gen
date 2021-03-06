/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.extension.gen;

import java.io.File;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.seasar.doma.extension.gen.internal.util.ClassUtil;
import org.seasar.doma.extension.gen.internal.util.FileUtil;

/**
 * SQLテスト記述のファクトリです。
 * 
 * @author taedium
 */
public class SqlTestDescFactory {

    /** SQLのテストクラス名 */
    protected final String sqlTestClassName;

    /** 抽象クラスの場合{@code true} */
    protected final boolean abstrct;

    /** {@code org.seasar.doma.jdbc.dialect.Dialect}のサブクラスの名前 */
    protected final String dialectClassName;

    /** {@link Driver} のサブクラスの名前 */
    protected final String driverClassName;

    /** JDBC接続URL */
    protected final String url;

    /** JDBC接続ユーザ */
    protected final String user;

    /** JDBC接続パスワード */
    protected final String password;

    /** テスト対象SQLファイルのセット */
    protected final Set<File> sqlFiles = new TreeSet<File>();

    /** クラス記述のサポートクラス */
    protected final ClassDescSupport classDescSupport = new ClassDescSupport();

    /**
     * インスタンスを構築します。
     * 
     * @param sqlTestClassName
     *            SQLのテストクラス名
     * @param abstrct
     *            抽象クラスの場合{@code true}
     * @param dialectClassName
     *            {@code org.seasar.doma.jdbc.dialect.Dialect}のサブクラスの名前
     * @param driverClassName
     *            {@link Driver} のサブクラスの名前
     * @param url
     *            JDBC接続URL
     * @param user
     *            JDBC接続ユーザ
     * @param password
     *            JDBC接続パスワード
     * @param sqlFiles
     *            テスト対象のSQLファイルのセット
     */
    public SqlTestDescFactory(String sqlTestClassName, boolean abstrct,
            String dialectClassName, String driverClassName, String url,
            String user, String password, Set<File> sqlFiles) {
        if (sqlTestClassName == null) {
            throw new GenNullPointerException("sqlTestClassName");
        }
        if (dialectClassName == null) {
            throw new GenNullPointerException("dialectClassName");
        }
        if (driverClassName == null) {
            throw new GenNullPointerException("driverClassName");
        }
        if (url == null) {
            throw new GenNullPointerException("url");
        }
        if (user == null) {
            throw new GenNullPointerException("user");
        }
        if (password == null) {
            throw new GenNullPointerException("password");
        }
        if (sqlFiles == null) {
            throw new GenNullPointerException("sqlFiles");
        }
        this.sqlTestClassName = sqlTestClassName;
        this.abstrct = abstrct;
        this.dialectClassName = dialectClassName;
        this.driverClassName = driverClassName;
        this.url = url;
        this.user = user;
        this.password = password;
        this.sqlFiles.addAll(sqlFiles);
    }

    /**
     * SQLテスト記述を作成します。
     * 
     * @return SQLテスト記述
     */
    public SqlTestDesc createSqlFileTestDesc() {
        SqlTestDesc sqlTestDesc = new SqlTestDesc();
        sqlTestDesc.setPackageName(ClassUtil.getPackageName(sqlTestClassName));
        sqlTestDesc.setSimpleName(ClassUtil.getSimpleName(sqlTestClassName));
        sqlTestDesc.setAbstrct(abstrct);
        sqlTestDesc.setDialectClassName(dialectClassName);
        sqlTestDesc.setDriverClassName(driverClassName);
        sqlTestDesc.setUrl(url);
        sqlTestDesc.setUser(user);
        sqlTestDesc.setPassword(password);
        sqlTestDesc.setTemplateName(Constants.SQL_TEST_TEMPLATE);
        handleSqlFilePath(sqlTestDesc);
        return sqlTestDesc;
    }

    /**
     * SQLファイルのパスを扱います。
     * 
     * @param sqlTestDesc
     *            SQLテスト記述
     */
    protected void handleSqlFilePath(SqlTestDesc sqlTestDesc) {
        List<String> sqlFilePaths = new ArrayList<String>(sqlFiles.size());
        for (File file : sqlFiles) {
            if (file == null) {
                continue;
            }
            if (!file.isFile()) {
                continue;
            }
            if (!file.getName().endsWith(".sql")) {
                continue;
            }
            if (file.getName().contains("-")) {
                continue;
            }
            String canonicalPath = FileUtil.getCanonicalPath(file);
            String path = canonicalPath.replace(File.separator, "/");
            int pos = path.indexOf("/META-INF/");
            if (pos < 0) {
                continue;
            }
            sqlFilePaths.add(path.substring(pos + 1));
        }
        Collections.sort(sqlFilePaths);
        sqlTestDesc.setSqlFilePaths(sqlFilePaths);
    }
}
