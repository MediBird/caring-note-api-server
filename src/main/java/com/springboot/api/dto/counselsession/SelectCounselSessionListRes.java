package com.springboot.api.dto.counselsession;

import java.util.List;

public record SelectCounselSessionListRes(List<SelectCounselSessionListItem> sessionListItems,String nextCursor, boolean hasNext){}
