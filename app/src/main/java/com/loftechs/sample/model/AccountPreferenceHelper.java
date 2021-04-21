package com.loftechs.sample.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Strings;
import com.loftechs.sample.SampleApp;
import com.loftechs.sample.model.data.AccountEntity;
import com.loftechs.sample.utils.JsonConvertUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AccountPreferenceHelper {
    public static final String PREF_ACCOUNT = "pref_account";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private Map<String, AccountEntity> mAccountMap;

    private static class LazyHolder {
        private static final AccountPreferenceHelper instance = new AccountPreferenceHelper();
    }

    public static AccountPreferenceHelper getInstance() {
        return LazyHolder.instance;
    }

    public Map<String, AccountEntity> getAccountMap() {
        if (mAccountMap == null) {
            mAccountMap = getAccountEntities();
        }
        return mAccountMap;
    }

    public SharedPreferences getPref() {
        if (null == mPref) {
            mPref = PreferenceManager.getDefaultSharedPreferences(SampleApp.context);
        }
        return mPref;
    }

    public SharedPreferences.Editor getEditor() {
        if (null == mEditor) {
            mEditor = getPref().edit();
        }
        return mEditor;
    }

    public void setAccountEntity(AccountEntity accountEntity) {
        getAccountMap().put(accountEntity.getAccount(), accountEntity);
        try {
            String json = JsonConvertUtil.obj2json(getAccountMap());
            getEditor().putString(PREF_ACCOUNT, json).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, AccountEntity> getAccountEntities() {
        String json = getPref().getString(PREF_ACCOUNT, "");
        if (!Strings.isNullOrEmpty(json)) {
            try {
                return JsonConvertUtil.json2map(json, AccountEntity.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    public AccountEntity geFirstAccount() {
        Set<String> accountSet = getAccountEntities().keySet();
        if (accountSet.isEmpty()) {
            return null;
        }
        return (AccountEntity) getAccountEntities().values().toArray()[0];
    }

    public boolean isExistAccount(String account) {
        return getAccountEntities().containsKey(account);
    }

    public void clearAccount() {
        getEditor().putString(PREF_ACCOUNT, "").commit();
    }
}
