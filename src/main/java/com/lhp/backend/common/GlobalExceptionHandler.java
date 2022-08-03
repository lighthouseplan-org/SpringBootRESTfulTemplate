package com.lhp.backend.common;

import com.lhp.backend.constant.HeaderKey;
import com.lhp.backend.i18n.LocalLanguage;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

  @Autowired
  private LocalLanguage messageSource;

  /**
   * 处理其他异常
   * 
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public BaseResponse<Object> exceptionHandler(
      HttpServletRequest req,
      Exception e) {
    e.printStackTrace();
    logger.error("exceptionHandler ++++ error" + e.toString());
    BaseResponse<Object> ret = new BaseResponse<Object>();
    ret.setCode(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
    String lang = req.getHeader(HeaderKey.localLanguage);
    String msg = messageSource.getMessageStr(lang, "globalerror");
    ret.setMessage(msg + ":" + e.toString());
    return ret;
  }
}
