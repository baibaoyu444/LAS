package cn.las.controller;

import cn.las.bean.entity.*;
import cn.las.service.*;
import cn.las.utils.CSVUtil;
import cn.las.utils.DealExcelUtils;
import cn.las.utils.FileProcessUtils;
import cn.las.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("file")
@Api(tags = "文件上传接口")
public class FileUploadController {

    @Autowired
    CourseService courseService;

    @Autowired
    UserService userService;

    @Autowired
    LaboratoryService laboratoryService;

    @Autowired
    IClassService classService;

    @Autowired
    RoleService roleService;

    @RequestMapping(value = "uploadFiles", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(
            httpMethod = "POST",
            notes = "上传系统文件功能</br>"+
                    "输入Form-Data数据",
            value = "上传系统数据"
    )
    public Message uploadFiles(@RequestParam("file") MultipartFile [] file) {

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < file.length; i++) {
            String originalFilename = file[i].getOriginalFilename();
            String upload = FileProcessUtils.upload(file[i]);
            if(upload == null) {
                buffer.append(originalFilename + "文件有错误:");
                return new Message(403, "文件上传失败: " + originalFilename);
            }

            // 把文件名称更换为.csv
            String newFileName = new StringBuffer()
                    .append(upload.substring(0, upload.lastIndexOf(".")))
                    .append(".csv").toString();

            // 使用转换工具把excel文件转换为.csv文件
            DealExcelUtils.excelToCsv(upload, newFileName);

            // 删除上传的文件
            File uploadFile = new File(upload);
            uploadFile.delete();

            // 遍历链表当中的数据
            List<String> list = CSVUtil.importCsv(new File(newFileName));

            // 执行list的处理操作
            if(originalFilename.contains("用户")) {
                String s = uploadUserFile(list);
                if(s != null)
                buffer.append(s);
            }
            if(originalFilename.contains("班级")) {
                String s = uploadClassFile(list);
                if(s != null)
                buffer.append(s);
            }
            if(originalFilename.contains("实验室")) {
                String s = uploadLabratoryFile(list);
                if(s != null)
                buffer.append(s);
            }
            if(originalFilename.contains("课程")) {
                String s = uploadCourseFile(list);
                if(s != null)
                buffer.append(s);
            }
        }

        if(buffer.length() == 0) return new Message(200, "文件上传成功");

        return new Message(403, buffer.toString());
    }


    /**
     * 上传user文件的功能
     *
     * @param list
     * @return
     */
    public String uploadUserFile(List<String> list) {
        String[] split = null;

        // 保存错误信息使用
        StringBuffer buffer = null;

        int idx = 1;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");

                // 判断表头的正确/错误信息
                if(     !split[0].trim().equals("用户名") ||
                        !split[1].trim().equals("密码") ||
                        !split[2].trim().equals("教师名称"))
                return new StringBuffer().append("用户表头存在错误 : ").append(s).toString();

                continue;
            }
            split = s.split(",");

            if(!isValid(split)) {
                if(buffer == null) buffer = new StringBuffer().append("用户表存在错误格式:").append(" ");
                buffer.append(String.format("错误数据: line-%d { %s }", idx, s)).append(" ");
                continue;
            }

            idx++;

            try {
                // 如果用户存在，跳过插入数据
                User user = userService.findByUsername(split[0]);
                if(user != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的user信息
            User user = new User();
            user.setUsername(split[0]);
            user.setPassword(MD5Utils.MD5Encode(split[1]));
            user.setTeacher(split[2]);
            try {
                userService.addUser(user);

                // 生成新的role的信息 身份信息是user
                User usr = userService.findByUsername(split[0]);
                roleService.insertUserRole(usr.getId(), 2);
            } catch (Exception e) {
                buffer.append(user.toString()).append(" ");
            }
        }

        // 返回冲突数据信息
        if(buffer == null || buffer.length() != 0) {
            return null;
        }

        return buffer.toString();
    }

    public String uploadCourseFile(List<String> list) {
        StringBuffer buffer = null;
        String[] split = null;

        int idx = 1;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");

                // 判断表头的正确/错误信息
                if(     !split[0].trim().equals("课程名") ||
                        !split[1].trim().equals("课时"))
                    return new StringBuffer().append("课程表头存在错误 : ").append(s).toString();

                continue;
            }
            split = s.split(",");

            if(!isValid(split)) {
                if(buffer == null) buffer = new StringBuffer().append("课程表存在错误格式:").append(" ");
                buffer.append(String.format("错误数据: line-%d { %s }", idx, s)).append(" ");
                continue;
            }

            idx++;

            try {
                // 如果用户存在，跳过插入数据
                Course course = courseService.findCourseByCourseName(split[0]);
                if(course != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的course信息
            Course course = new Course();
            course.setName(split[0]);
            course.setTime(Integer.parseInt(split[1]));
            try {
                courseService.addCourse(course);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 返回冲突数据信息
        if(buffer == null || buffer.length() != 0) {
            return null;
        }

        return buffer.toString();
    }

    public String uploadClassFile(List<String> list) {
        // 记录冲突数据
        StringBuffer buffer = null;
        String[] split = null;

        int idx = 1;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");

                // 判断表头的正确/错误信息
                if(     !split[0].trim().equals("班级名称") ||
                        !split[1].trim().equals("班级人数"))
                    return new StringBuffer().append("班级表头存在错误 : ").append(s).toString();

                continue;
            }
            split = s.split(",");

            if(!isValid(split)) {
                if(buffer == null) buffer = new StringBuffer().append("班级表存在错误格式:").append(" ");
                buffer.append(String.format("错误数据: line-%d { %s }", idx, s)).append(" ");
                continue;
            }

            idx++;

            try {
                // 如果班级信息存在，跳过插入数据
                if(classService.findByClassName(split[0]) != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的iclass信息
            IClass iClass = new IClass();
            iClass.setName(split[0]);
            iClass.setNumber(Integer.parseInt(split[1]));
            try {
                // 添加班级IClass信息
                classService.addClass(iClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 返回冲突数据信息
        if(buffer == null || buffer.length() != 0) {
            return null;
        }

        return buffer.toString();
    }

    public String uploadLabratoryFile(List<String> list) {

        // 记录冲突数据
        StringBuffer buffer = null;
        String[] split = null;

        int idx = 1;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");

                // 判断表头的正确/错误信息
                if(     !split[0].trim().equals("实验室名称") ||
                        !split[1].trim().equals("实验室类型") ||
                        !split[2].trim().equals("实验室大小") ||
                        !split[3].trim().equals("实验室位置"))
                    return new StringBuffer().append("实验室表头存在错误 : ").append(s).toString();

                continue;
            }
            split = s.split(",");

            if(!isValid(split)) {
                if(buffer == null) buffer = new StringBuffer().append("实验室表存在错误格式:").append(" ");
                buffer.append(String.format("错误数据: line-%d { %s }", idx, s)).append(" ");
                continue;
            }

            idx++;

            try {
                if(laboratoryService.findByLaboratoryName(split[0]) != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的laboratory信息
            Laboratory lab = new Laboratory();
            lab.setName(split[0]);
            lab.setType(split[1]);
            lab.setSize(Integer.parseInt(split[2]));
            lab.setLocation(split[3]);
            lab.setStatus(1);

            try {
                // 添加实验室Laboratory信息
                laboratoryService.addLaboratory(lab);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 返回冲突数据信息
        if(buffer == null || buffer.length() != 0) {
            return null;
        }

        return buffer.toString();
    }

































    @RequestMapping(value = "uploadUsers", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message uploadUserFile(@RequestParam("file") MultipartFile multipartFile) {

        String filename = "用户测试数据";
        // 获取参数并且进行非空验证
        if(filename == null || multipartFile == null) {
            return new Message(403, "参数非空");
        }

        // 开始进行文件上传
        String upload = FileProcessUtils.upload(multipartFile);
        if(upload == null) {
            return new Message(501, "列表上传失败");
        }

        // 把文件名称更换为.csv
        String newFileName = new StringBuffer()
                .append(upload.substring(0, upload.lastIndexOf(".")))
                .append(".csv").toString();

        // 使用转换工具把excel文件转换为.csv文件
        DealExcelUtils.excelToCsv(upload, newFileName);

        // 删除上传的文件
        File file = new File(upload);
        file.delete();

        List<String> list = CSVUtil.importCsv(new File(newFileName));
        String[] split = null;
        StringBuffer buffer = new StringBuffer();

        for (String s : list) {
            if(split == null) {
                split = s.split(",");
                continue;
            }
            split = s.split(",");

            if(!isValid(split)) continue;

            try {
                // 如果用户存在，跳过插入数据
                User user = userService.findByUsername(split[0]);
                if(user != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的user信息
            User user = new User();
            user.setUsername(split[0]);
            user.setPassword(MD5Utils.MD5Encode(split[1]));
            user.setTeacher(split[2]);
            try {
                userService.addUser(user);

                // 生成新的role的信息 身份信息是user
                User usr = userService.findByUsername(split[0]);
                roleService.insertUserRole(usr.getId(), 2);
            } catch (Exception e) {
                buffer.append(user.toString()).append(" ");
            }
        }

        // 返回冲突数据信息
        if(buffer.length() != 0) {
            return new Message(403, "下列信息错误，请更正: " + buffer.toString());
        }

        return new Message(200, "数据更新成功");
    }

    @RequestMapping("uploadCourses")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message uploadCourseFile(@RequestParam("file") MultipartFile multipartFile) {

        String filename = "课程测试数据";
        // 获取参数并且进行非空验证
        if(filename == null || multipartFile == null) {
            return new Message(403, "参数非空");
        }

        // 开始进行文件上传
        String upload = FileProcessUtils.upload(multipartFile);
        if(upload == null) {
            return new Message(501, "列表上传失败");
        }

        // 把文件名称更换为.csv
        String newFileName = new StringBuffer()
                .append(upload.substring(0, upload.lastIndexOf(".")))
                .append(".csv").toString();

        // 使用转换工具把excel文件转换为.csv文件
        DealExcelUtils.excelToCsv(upload, newFileName);

        // 删除上传的文件
        File file = new File(upload);
        file.delete();

        // 记录冲突数据
        StringBuffer buffer = new StringBuffer();

        // 遍历链表当中的数据
        List<String> list = CSVUtil.importCsv(new File(newFileName));
        String[] split = null;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");
                continue;
            }
            split = s.split(",");

            if(!isValid(split)) continue;

            // 如果课程存在，直接跳过
            try {
                if(courseService.findCourseByCourseName(split[0]) != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的user信息
            Course course = new Course();
            course.setName(split[0]);
            course.setTime(Integer.parseInt(split[1]));
            try {
                // 添加Course信息
                courseService.addCourse(course);
            } catch (Exception e) {
                e.printStackTrace();
                return new Message(501, "课程信息更新失败");
            }
        }

        return new Message(200, "数据更新成功");
    }


    @RequestMapping("uploadClass")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message uploadClassFile(@RequestParam("file") MultipartFile multipartFile) {

        String filename = "班级测试数据";
        // 获取参数并且进行非空验证
        if(filename == null || multipartFile == null) {
            return new Message(403, "参数非空");
        }

        // 开始进行文件上传
        String upload = FileProcessUtils.upload(multipartFile);
        if(upload == null) {
            return new Message(501, "列表上传失败");
        }

        // 把文件名称更换为.csv
        String newFileName = new StringBuffer()
                .append(upload.substring(0, upload.lastIndexOf(".")))
                .append(".csv").toString();

        // 使用转换工具把excel文件转换为.csv文件
        DealExcelUtils.excelToCsv(upload, newFileName);

        // 删除上传的文件
        File file = new File(upload);
        file.delete();

        // 记录冲突数据
        StringBuffer buffer = new StringBuffer();

        // 遍历链表当中的数据
        List<String> list = CSVUtil.importCsv(new File(newFileName));
        String[] split = null;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");
                continue;
            }
            split = s.split(",");

            if(!isValid(split)) continue;

            // 如果课程存在，直接跳过
            try {
                if(classService.findByClassName(split[0]) != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的user信息
            IClass iClass = new IClass();
            iClass.setName(split[0]);
            iClass.setNumber(Integer.parseInt(split[1]));
            System.out.println(iClass);
            try {
                // 添加班级IClass信息
                classService.addClass(iClass);
            } catch (Exception e) {
                e.printStackTrace();
                return new Message(501, "班级信息更新失败");
            }
        }

        return new Message(200, "数据更新成功");
    }

    @RequestMapping("uploadLabs")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Message uploadLabratoryFile(@RequestParam("file") MultipartFile multipartFile) {

        String filename = "实验室测试数据";
        // 获取参数并且进行非空验证
        if(filename == null || multipartFile == null) {
            return new Message(403, "参数非空");
        }

        // 开始进行文件上传
        String upload = FileProcessUtils.upload(multipartFile);
        if(upload == null) {
            return new Message(501, "列表上传失败");
        }

        // 把文件名称更换为.csv
        String newFileName = new StringBuffer()
                .append(upload.substring(0, upload.lastIndexOf(".")))
                .append(".csv").toString();

        // 使用转换工具把excel文件转换为.csv文件
        DealExcelUtils.excelToCsv(upload, newFileName);

        // 删除上传的文件
        File file = new File(upload);
        file.delete();

        // 记录冲突数据
        StringBuffer buffer = new StringBuffer();

        // 遍历链表当中的数据
        List<String> list = CSVUtil.importCsv(new File(newFileName));

        String[] split = null;
        for (String s : list) {
            if(split == null) {
                split = s.split(",");
                continue;
            }
            split = s.split(",");

            if(!isValid(split)) continue;

            // 如果课程存在，直接跳过
            try {
                if(laboratoryService.findByLaboratoryName(split[0]) != null) continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 生成新的user信息
            Laboratory lab = new Laboratory();
            lab.setName(split[0]);
            lab.setType(split[1]);
            lab.setSize(Integer.parseInt(split[2]));
            lab.setLocation(split[3]);
            lab.setStatus(1);

            try {
                // 添加实验室Laboratory信息
                laboratoryService.addLaboratory(lab);
            } catch (Exception e) {
                e.printStackTrace();
                return new Message(501, "课程信息更新失败");
            }
        }

        return new Message(200, "数据更新成功");
    }


    private boolean isValid(String [] split) {
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            if(split[i].length() == 0) {
                return false;
            }
        }
        return true;
    }
}
