package com.anuragroy.model;

public class DeptSelector {
    ///String array to save the departments
    private final static String[] DEPTS = {
            "GENERAL MEDICINE",
            "GENERAL SURGERY",
            "CARDIOLOGY",
            "NEUROLOGY",
            "GASTROENTEROLOGY",
            "GYNAECOLOGY & OBSTETRICS",
            "PEDIATRICS",
            "ORTHOPEDICS",
            "OTOLARYNGOLOGY",
            "OPHTHALMOLOGY",
            "DERMATOLOGY",
            "PMR"
    };

    //String array to store general complains
    private final static String[] COMPLAINS = {
            "Fever/Headache/Vertigo/Nausea",
            "Abdomen Pain/Vomiting/Fever",
            "Pain in left Shoulder/Chest with/wo Vertigo",
            "Palpitation",
            "Insomnia",
            "Unconsciousness/Blackout/Paralysis",
            "Head-Trauma/Vomiting",
            "Convulsion",
            "Loose Motion/Mucus with Stool",
            "Flatulence/Gas in Abdomen",
            "Hyper-Acidity",
            "Pregnant Woman",
            "STD",
            "Children/Below 12 Years",
            "Low Back Pain",
            "Pain in Joints",
            "Trauma in Limbs with Swelling & Pain",
            "Nose Bleeding",
            "Common Cold",
            "Throat Pain/Difficulty in deglutition",
            "Eye Problems",
            "Skin Itching",
            "Discoloration of skin",
            "Hairfall/Dandruff in Scalp",
            "Other Skin Problems",
            "Physiotherapy for Paralyzed Patient",
            "Exercise"
    };

    public static String[] getDEPTS() {
        return DEPTS;
    }

    public static String[] getCOMPLAINS() {
        return COMPLAINS;
    }

    //Store the Doctor Dept. as per the patient's complain
    public static String setDepartment(String patientComplain) {
        switch(patientComplain){
            case "Fever/Headache/Vertigo/Nausea":
                return DEPTS[0];
            case "Abdomen Pain/Vomiting/Fever":
                return DEPTS[1];
            case "Pain in left Shoulder/Chest with/wo Vertigo": case "Palpitation": case "Insomnia":
                return DEPTS[2];
            case "Unconsciousness/Blackout/Paralysis": case "Head-Trauma/Vomiting": case "Convulsion":
                return DEPTS[3];
            case "Loose Motion/Mucus with Stool": case "Flatulence/Gas in Abdomen": case "Hyper-Acidity":
                return DEPTS[4];
            case "Pregnant Woman": case "STD":
                return DEPTS[5];
            case "Children/Below 12 Years":
                return DEPTS[6];
            case "Low Back Pain": case "Pain in Joints": case "Trauma in Limbs with Swelling & Pain":
                return DEPTS[7];
            case "Nose Bleeding": case "Common Cold": case "Throat Pain/Difficulty in deglutition":
                return DEPTS[8];
            case "Eye Problems":
                return DEPTS[9];
            case "Skin Itching": case "Discoloration of skin": case "Hairfall/Dandruff in Scalp": case "Other Skin Problems":
                return DEPTS[10];
            case "Physiotherapy for Paralyzed Patient": case "Exercise":
                return DEPTS[11];
        }
        return null;
    }

}
