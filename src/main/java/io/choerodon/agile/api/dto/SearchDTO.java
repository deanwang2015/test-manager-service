package io.choerodon.agile.api.dto;

import io.choerodon.agile.infra.common.utils.StringUtil;

import javax.persistence.Transient;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/31
 */
public class SearchDTO {

    /**
     * 输入查询参数
     */
    private Map<String, Object> searchArgs;

    /**
     * 过滤查询参数
     */
    private Map<String, Object> advancedSearchArgs;

	@Transient
	private String[] executionStatus;

    public Map<String, Object> getSearchArgs() {
        return searchArgs;
    }

    public void setSearchArgs(Map<String, Object> searchArgs) {
        this.searchArgs = searchArgs;
    }

    public Map<String, Object> getAdvancedSearchArgs() {
        return advancedSearchArgs;
    }

    public void setAdvancedSearchArgs(Map<String, Object> advancedSearchArgs) {
        this.advancedSearchArgs = advancedSearchArgs;
    }

	public String[] getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String[] executionStatus) {
		this.executionStatus = executionStatus;
	}

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
