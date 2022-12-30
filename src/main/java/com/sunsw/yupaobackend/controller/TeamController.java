package com.sunsw.yupaobackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunsw.yupaobackend.common.BaseResponse;
import com.sunsw.yupaobackend.common.ErrorCode;
import com.sunsw.yupaobackend.common.ResultUtil;
import com.sunsw.yupaobackend.exception.BusinessException;
import com.sunsw.yupaobackend.model.domain.Team;
import com.sunsw.yupaobackend.model.domain.User;
import com.sunsw.yupaobackend.model.dto.TeamQuery;
import com.sunsw.yupaobackend.model.request.*;
import com.sunsw.yupaobackend.model.vo.TeamUserVO;
import com.sunsw.yupaobackend.service.TeamService;
import com.sunsw.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.sunsw.yupaobackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 队伍接口
 */
@RestController
@RequestMapping("/team")
@Slf4j
//@CrossOrigin(origins = { "http://localhost:3000" },methods = {RequestMethod.POST,RequestMethod.GET})
//前端axios设置axios.defaults.withCredentials = true;//表示向后端发送请求时携带请求的凭证
//@CrossOrigin无效，通过拦截器中设置response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private RedisTemplate redisTemplate;
    /**
     *
     * @param team 添加队伍
     * @return 队伍id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtil.success(teamId);
    }

    /**
     *
     * @param team 更新队伍
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if(!result){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtil.success(true);
    }
    /**
     * @param id 查询单个队伍
     * @return team
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById( long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtil.success(team);
    }

    /**
     * @param  teamQuery 查询多个队伍
     * @return team
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams (TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);
        return ResultUtil.success(teamList);
    }
    /**
     * @param  teamQuery 分页查询多个队伍
     * @return team
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage (TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page = new Page(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);

        Page<Team> resultPage = teamService.page(page,queryWrapper);
        return ResultUtil.success(resultPage);
    }
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.joinTeam(teamJoinRequest,loginUser);
        return ResultUtil.success(result);
    }
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest,HttpServletRequest request){
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtil.success(result);
    }
    /**
     *
     * @param id 删除队伍
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id,HttpServletRequest request){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id,loginUser);
        if(!result){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtil.success(true);
    }
}
