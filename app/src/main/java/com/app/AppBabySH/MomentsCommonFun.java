package com.app.AppBabySH;

import android.view.View;
import android.view.ViewGroup;

import com.app.AppBabySH.activity.MainTabActivity;
import com.app.Common.MyAlertDialog;
import com.app.Common.UserMstr;
import com.app.Common.WebService;
import com.umeng.socialize.utils.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ray on 2015/6/9.
 */
public class MomentsCommonFun {
    final private String TAG = "momentsCommentFun";
    private MomentsFragment momentsF;
    private MainTabActivity main;
    private View rootView;

    public MomentsCommonFun(MomentsFragment _main, View _view) {
        momentsF = _main;
        rootView = _view;
        main = (MainTabActivity) _main.getActivity();
    }

    //  刪除班級圈
    public void delCircle() {
        MyAlertDialog.ShowNYDialog("确定要删除该讯息?", "确定", "取消", momentsF.getActivity(), new MyAlertDialog.NYDialogCallBack() {
            @Override
            public void onEnter() {
                MyAlertDialog.ShowLoading(momentsF.getActivity(), "删除中...");
                WebService.SetCircleDelete(null, momentsF.callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
                    @Override
                    public void CompleteCallback(String id, Object obj) {
                        MyAlertDialog.Cancel();
                        Log.i(TAG, "SetCircleDelete obj : " + obj);
                        try {
                            if (obj.toString().equals("1")) {
                                MyAlertDialog.ShowOKDialog("班级圈已删除", momentsF.getActivity(), new MyAlertDialog.OKDialogCallBack() {
                                    @Override
                                    public void onEnter() {
                                        for (int i = 0; i < momentsF.momentslist.size(); i++) {
                                            if (momentsF.callBackItem.CIRCLE_ID.equals(momentsF.momentslist.get(i).CIRCLE_ID)) {
                                                momentsF.momentslist.remove(i);
                                            }
                                        }
                                        momentsF.adapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                MyAlertDialog.Show(momentsF.getActivity(), "删除失败!");
                            }
                        } catch (Exception e) {
                            MyAlertDialog.Show(momentsF.getActivity(), "SetCircleDelete 删除失败!e:" + e);
                            e.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
    }

    //  對班級圈按讚
    public void goodCircle() {
        MyAlertDialog.ShowLoading(momentsF.getActivity(), "检查中...");
        WebService.GetCircleHadGood(null, momentsF.callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                MyAlertDialog.Cancel();
                try {
                    if (obj == null) {
                        MyAlertDialog.Show(momentsF.getActivity(), "Error!");
                        return;
                    }else if (obj.equals("Success! No Data Return!")) {
                        MyAlertDialog.Show(momentsF.getActivity(), "读取完成，无资料返回!");
                        return;
                    }
                    JSONObject json = (JSONObject) obj;
                    final String callType = json.optString("GOOD_CNT").equals("1") ? "CLEAR" : "INSERT";
                    WebService.SetCircleGood(null, momentsF.callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), callType, new WebService.WebCallback() {
                        @Override
                        public void CompleteCallback(String id, Object obj) {
                            MyAlertDialog.Cancel();
                            try {
                                if (obj == null) {
                                    MyAlertDialog.Show(momentsF.getActivity(), "提交失败!");
                                    return;
                                } else {
                                    if (callType.equals("INSERT")) {
                                        Map map = new HashMap();
                                        map.put("CIRCLE_ID", momentsF.callBackItem.CIRCLE_ID);
                                        map.put("USER_ID", UserMstr.userData.getUserID());
                                        map.put("NIC_NAME", UserMstr.userData.getBaseInfoAry().optJSONObject(0).optString("NIC_NAME"));
                                        JSONObject newJsonObj = new JSONObject(map);
                                        momentsF.callBackItem.GOOD.add(newJsonObj);
                                        MyAlertDialog.Toast(momentsF.getActivity(), "完成按讚!");
                                    } else {
                                        int i = -1;
                                        while (++i < momentsF.callBackItem.GOOD.size()) {
                                            if (momentsF.callBackItem.GOOD.get(i).optString("CIRCLE_ID").equals(momentsF.callBackItem.CIRCLE_ID) &&
                                                    momentsF.callBackItem.GOOD.get(i).optString("USER_ID").equals(UserMstr.userData.getUserID())) {
                                                momentsF.callBackItem.GOOD.remove(i);
                                            }
                                        }
                                        MyAlertDialog.Toast(momentsF.getActivity(), "取消按讚!");
                                    }
                                }
                                momentsF.adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                MyAlertDialog.Show(momentsF.getActivity(), "SetCircleGood 提交失败!e:" + e);
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    MyAlertDialog.Show(momentsF.getActivity(), "GetCircleHadGood Error!e:" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    //  收藏班級圈
    public void favCircle() {
        MyAlertDialog.ShowLoading(momentsF.getActivity(), "检查中...");
        WebService.GetCircleHadKeepToGrow(null, momentsF.callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), new WebService.WebCallback() {
            @Override
            public void CompleteCallback(String id, Object obj) {
                try {
                    if (obj == null) {
                        MyAlertDialog.Cancel();
                        MyAlertDialog.Show(momentsF.getActivity(), "Error!");
                        return;
                    }else if (obj.equals("Success! No Data Return!")) {
                        MyAlertDialog.Show(momentsF.getActivity(), "读取完成，无资料返回!");
                        return;
                    }
                    JSONObject json = (JSONObject) obj;
                    final String callType = json.optString("GROW_CNT").equals("1") ? "CLEAR" : "INSERT";
                    WebService.SetCircleKeepToGrow(null, momentsF.callBackItem.CIRCLE_ID, UserMstr.userData.getUserID(), callType, new WebService.WebCallback() {
                        @Override
                        public void CompleteCallback(String id, Object obj) {
                            MyAlertDialog.Cancel();
                            try {
                                if (obj == null) {
                                    MyAlertDialog.Show(momentsF.getActivity(), "SetCircleKeepToGrow 提交失败!");
                                    return;
                                } else {
                                    if (callType.equals("INSERT")) {
                                        MyAlertDialog.Show(momentsF.getActivity(), "完成收藏!");
                                    } else {
                                        MyAlertDialog.Toast(momentsF.getActivity(), "取消收藏!");
                                    }
                                }
                            } catch (Exception e) {
                                MyAlertDialog.Show(momentsF.getActivity(), "SetCircleKeepToGrow 提交失败!e:" + e);
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    MyAlertDialog.Show(momentsF.getActivity(), "GetCircleHadKeepToGrow Error!e:" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    //  條件顯示班級圈頁面
    public void customCircle(String $type) {
        MomentsCustomFragment personalF = new MomentsCustomFragment();
        personalF.Class_ID = momentsF.CurrClassID;
        if ($type.equals("personal")) {
            personalF.USER_ID = momentsF.callBackItem.USER_ID;
            personalF.NIC_NAME = momentsF.callBackItem.NIC_NAME;
            personalF.USER_AVATAR = momentsF.callBackItem.USER_AVATAR;
        } else if ($type.equals("news")) {
            personalF.USER_ID = UserMstr.userData.getUserID();
            personalF.NIC_NAME = "MomentsNews";
            personalF.USER_AVATAR = "";
        }

        main.OpenBottom(personalF);
    }
}
