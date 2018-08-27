package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseHistoryService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseHistoryServiceImpl implements TestCycleCaseHistoryService {
    @Autowired
    ITestCycleCaseHistoryService iTestCycleCaseHistoryService;

	@Autowired
	UserService userFeignClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO) {
        return ConvertHelper.convert(iTestCycleCaseHistoryService.insert(ConvertHelper.convert(testCycleCaseHistoryDTO, TestCycleCaseHistoryE.class)), TestCycleCaseHistoryDTO.class);

    }

    @Override
    public Page<TestCycleCaseHistoryDTO> query(Long cycleCaseId, PageRequest pageRequest) {
        TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
        historyDTO.setExecuteId(cycleCaseId);
        Page<TestCycleCaseHistoryE> serviceEPage = iTestCycleCaseHistoryService.query(ConvertHelper.convert(historyDTO, TestCycleCaseHistoryE.class), pageRequest);
		Page<TestCycleCaseHistoryDTO> dto = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseHistoryDTO.class);
		Long[] users = dto.stream().map(TestCycleCaseHistoryDTO::getLastUpdatedBy).filter(u->u!=null && !u.equals(0L)).distinct().toArray(Long[]::new);
		Map user = userFeignClient.query(users);
		setUser(dto, user);
		return dto;
	}


	private void setUser(List<TestCycleCaseHistoryDTO> dto, Map<Long, UserDO> users) {
    	if(ObjectUtils.isEmpty(users)){
    		return;
		}
		dto.forEach(v -> {
			if (v.getLastUpdatedBy() != null && !v.getLastUpdatedBy().equals(0L)) {
				v.setUser(users.get(v.getLastUpdatedBy()));
			}
		});
    }

    @Override
    public TestCycleCaseHistoryDTO createAssignedHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase){
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(beforeCycleCase.getExecuteId());
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_ASSIGNED);
		Long after = afterCycleCase.getAssignedTo();
		Long before = beforeCycleCase.getAssignedTo();
		Long[] para = new Long[]{before, after};
		Map<Long, UserDO> users = userFeignClient.query(para);

		if (before != null && before.longValue() != 0) {
			UserDO u = users.get(before);
			historyDTO.setOldValue(u.getLoginName() + u.getRealName());
		} else {
			historyDTO.setOldValue(TestCycleCaseHistoryE.FIELD_NULL);
		}
		if (before != null && after.longValue() != 0) {
			UserDO u = users.get(after);
			historyDTO.setNewValue(u.getLoginName() + u.getRealName());
		} else {
			historyDTO.setNewValue(TestCycleCaseHistoryE.FIELD_NULL);
		}
		return historyDTO;
	}

	@Override
	public TestCycleCaseHistoryDTO createStatusHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase){
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(beforeCycleCase.getExecuteId());
		String newColor = afterCycleCase.getExecutionStatusName();
		String oldColor = beforeCycleCase.getExecutionStatusName();
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_STATUS);
		historyDTO.setNewValue(newColor);
		historyDTO.setOldValue(oldColor);
		return historyDTO;
	}

	@Override
	public TestCycleCaseHistoryDTO createCommentHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase){
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(beforeCycleCase.getExecuteId());
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_COMMENT);
		if (StringUtils.isEmpty(afterCycleCase.getComment())) {
			historyDTO.setNewValue(TestCycleCaseHistoryE.FIELD_NULL);
		} else {
			historyDTO.setNewValue(afterCycleCase.getComment());
		}
		if (StringUtils.isEmpty(beforeCycleCase.getComment())) {
			historyDTO.setOldValue(TestCycleCaseHistoryE.FIELD_NULL);
		} else {
			historyDTO.setOldValue(beforeCycleCase.getComment());
		}
		return historyDTO;
	}

}
