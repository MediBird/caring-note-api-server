package com.springboot.api.dto.counselsession;

import java.util.List;

public record SelectCounselSessionGuestListRes(List<SelectCounselSessionGuestListItem> sessionGuestListItems, String nextCursor, boolean hasNext){}
