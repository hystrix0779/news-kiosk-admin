package com.cool;

import com.cool.core.code.CodeGenerator;
import com.cool.core.code.CodeModel;
import com.cool.core.code.CodeTypeEnum;
import com.cool.modules.kiosk.entity.KioskMachineEntity;
import com.cool.modules.kiosk.entity.KioskMachineErrEntity;
import com.cool.modules.kiosk.entity.KioskMachineLogEntity;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.entity.KioskOrderRefundEntity;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.mybatisflex.annotation.Table;
import java.util.List;

public class CoolCodeGeneratorTest {
    public static void main(String[] args) {
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.init();
        List<Class> list = List.of(KioskMachineEntity.class, 
        KioskMachineErrEntity.class,
        KioskOrderEntity.class, 
        KioskOrderRefundEntity.class, 
        KioskMachineLogEntity.class,
        KioskTemplateEntity.class);

        list.forEach(o -> {
            Table annotation = (Table) o.getAnnotation(Table.class);
            CodeModel codeModel = new CodeModel();
            codeModel.setType(CodeTypeEnum.ADMIN);
            codeModel.setName(annotation.comment());
            codeModel.setModule(getFirstWord(o.getSimpleName()));
            codeModel.setEntity(o);
            // 生成 controller
           codeGenerator.controller(codeModel);
            // 生成 mapper
            codeGenerator.mapper(codeModel);
            // 生成 service
            codeGenerator.service(codeModel);
        });
    }

    public static String getFirstWord(String className) {
        if (className == null || className.isEmpty()) {
            return "";
        }

        StringBuilder firstWord = new StringBuilder();
        boolean foundFirstWord = false;

        for (char c : className.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (foundFirstWord) {
                    break;
                }
                firstWord.append(c);
                foundFirstWord = true;
            } else {
                if (foundFirstWord) {
                    firstWord.append(c);
                }
            }
        }

        return firstWord.toString().toLowerCase();
    }
}
