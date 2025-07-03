package com.fdc.service;

import com.fdc.vo.share.ShareVO;

import java.util.List;

public interface ShareService {

    List<ShareVO> listShares(List<String> userIds, List<String> fileIds);

}
