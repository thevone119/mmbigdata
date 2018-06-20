package com.bingo.business.movie.action;

import com.bingo.common.exception.DaoException;
import com.bingo.common.exception.ServiceException;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018-06-03.
 */
@RestController
@Scope("prototype")
@RequestMapping("movie")
public class MovieCheckAciton {
    @ResponseBody
    @RequestMapping("/checkUnMarkZip")
    public XJsonInfo checkUnMarkZip() throws ServiceException, DaoException {
        return new XJsonInfo().setMsg("ok");
    }
}
