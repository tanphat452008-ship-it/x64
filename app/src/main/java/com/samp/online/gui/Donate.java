package com.samp.online.gui; // Khai báo package cho lớp này, giúp tổ chức code và tránh xung đột tên.

import static java.lang.String.format; // Import phương thức static `format` từ lớp `String` để có thể gọi trực tiếp `format(...)`.

import android.app.Activity; // Import lớp `Activity`, là một màn hình đơn lẻ mà người dùng có thể tương tác.

import com.samp.online.R; // Import lớp `R` tự động tạo, chứa ID của tất cả tài nguyên (layout, string, drawable,...).
import com.samp.online.gui.adapters.ActionsAdapter; // Import lớp `ActionsAdapter` tùy chỉnh, dùng để hiển thị danh sách các hành động.
import com.samp.online.gui.adapters.DonateAdapter; // Import lớp `DonateAdapter` tùy chỉnh, dùng để hiển thị danh sách các mục donate.
import com.samp.online.gui.models.Actions; // Import lớp `Actions` (model), đại diện cho dữ liệu của một hành động.
import com.samp.online.gui.adapters.DonatAdapter; // Import lớp `DonatAdapter` tùy chỉnh (có vẻ tên gần giống DonateAdapter).
import com.samp.online.gui.models.Donatee; // Import lớp `Donatee` (model), đại diện cho dữ liệu của một người nhận donate hoặc một mục donate.
import com.samp.online.gui.models.Donat; // Import lớp `Donat` (model), (có vẻ tên gần giống Donatee).


import com.nvidia.devtech.NvEventQueueActivity; // Import một lớp từ Nvidia, có thể liên quan đến xử lý sự kiện hoặc đồ họa.

import android.graphics.Color; // Import lớp `Color` để làm việc với màu sắc trong UI.
import android.view.View; // Import lớp `View`, là khối xây dựng cơ bản cho các thành phần UI.
import android.view.animation.AnimationUtils; // Import `AnimationUtils` để tải và áp dụng hiệu ứng animation.
import android.widget.*; // Import tất cả các lớp trong package `android.widget`, chứa các UI widget phổ biến (Button, TextView,...).

import androidx.appcompat.app.AppCompatActivity; // Import `AppCompatActivity` từ AndroidX, lớp cơ sở cho Activity có hỗ trợ action bar.
import androidx.constraintlayout.widget.ConstraintLayout; // Import `ConstraintLayout` từ AndroidX, một layout manager linh hoạt.
import androidx.recyclerview.widget.LinearLayoutManager; // Import `LinearLayoutManager` từ AndroidX, quản lý cách item được sắp xếp trong RecyclerView (theo hàng dọc/ngang).
import androidx.recyclerview.widget.RecyclerView; // Import `RecyclerView` từ AndroidX, dùng để hiển thị danh sách lớn một cách hiệu quả.

import java.text.DecimalFormat; // Import `DecimalFormat` để định dạng số thập phân.
import java.text.DecimalFormatSymbols; // Import `DecimalFormatSymbols` để tùy chỉnh các ký hiệu trong định dạng số.
import java.util.ArrayList; // Import `ArrayList`, một triển khai của `List` có thể thay đổi kích thước.
import java.util.Formatter; // Import `Formatter` để định dạng chuỗi output.
import java.util.List; // Import interface `List`, đại diện cho một tập hợp các phần tử có thứ tự.

import retrofit2.Call; // Import `Call` từ Retrofit, đại diện cho một yêu cầu mạng sẽ được thực thi.
import retrofit2.Callback; // Import `Callback` từ Retrofit, dùng để xử lý kết quả (thành công/thất bại) của yêu cầu mạng.
import retrofit2.Response; // Import `Response` từ Retrofit, đại diện cho phản hồi từ máy chủ.
import retrofit2.Retrofit; // Import `Retrofit`, thư viện HTTP client để giao tiếp với API.
import retrofit2.converter.gson.GsonConverterFactory; // Import `GsonConverterFactory` từ Retrofit, để chuyển đổi JSON sang đối tượng Java bằng Gson.

public class Donate extends AppCompatActivity { // Khai báo lớp `Donate` kế thừa từ `AppCompatActivity`, nghĩa là đây là một màn hình trong ứng dụng.

    static RecyclerView donateRecycler, donatRecycler, actionsRecycler; // Biến static cho các RecyclerViews (danh sách cuộn) để hiển thị các mục donate, donat (?), và hành động. `static` nghĩa là chúng thuộc về lớp, không phải đối tượng cụ thể.
    static DonateAdapter donateAdapter; // Adapter cho `donateRecycler`.
    static DonatAdapter donatAdapter; // Adapter cho `donatRecycler`.
    static ActionsAdapter actionsAdapter; // Adapter cho `actionsRecycler`.

    public Activity activity; // Tham chiếu đến Activity hiện tại mà lớp Donate này đang hoạt động.
    public ConstraintLayout donate_layout, donate_akijas, donate_item, donate_center, donate_down, donate_uslugi, donate_vip; // Các layout (vùng chứa) chính của màn hình donate và các phần tử con của nó.

    public LinearLayout sc_don, podt_don; // Các LinearLayout, thường dùng để sắp xếp các view con theo chiều dọc hoặc ngang.
    public ImageView donate_close, plusbc, plusrub, back, imgusl; // Các ImageView để hiển thị hình ảnh (nút đóng, thêm BC, thêm RUB, quay lại, hình ảnh dịch vụ).
    public Button sc_close, sc_close2, auto_button, usl_button1, usl_button2, uslugi_buy, button_okusl, button_nousl, vipsil, vipgld, vipplt; // Các Button (nút bấm) cho các hành động khác nhau (đóng, xác nhận, mua, chọn VIP,...).
    public TextView moneyy, bcc, keys, bp, nabori, aks, tovari, actija, vips, skins, sc1, sc2, auto, maintext, uslugi, topusl, costuscl, costpodtusl; // Các TextView để hiển thị văn bản (số tiền, số BC, tên mục,...).
    public RecyclerView autoRecycler, uslugiRecycler; // RecyclerViews để hiển thị danh sách xe và dịch vụ.
    public int money, bc, autooo, uslugi_status, carid, carcost; // Các biến kiểu số nguyên để lưu trữ dữ liệu như tiền, BC (đơn vị tiền tệ trong game?), trạng thái dịch vụ, ID xe, giá xe.
    public String costpodt, CarName; // Các biến kiểu chuỗi để lưu trữ chi phí xác nhận và tên xe.
    public Donate(Activity aactivity) { // Constructor của lớp Donate, được gọi khi một đối tượng Donate được tạo.
        activity = aactivity;
        donate_layout = activity.findViewById(R.id.donate_reytiz);
        donate_item = activity.findViewById(R.id.donate_item);
        donate_center = activity.findViewById(R.id.constraintLayout2);
        donate_akijas = activity.findViewById(R.id.constraintLayout3);
        donate_vip = activity.findViewById(R.id.constraintLayout4);
        donate_uslugi = activity.findViewById(R.id.uslugii);
        sc_don = activity.findViewById(R.id.donate_sc);
        podt_don = activity.findViewById(R.id.donate_podt);
        donate_close = activity.findViewById(R.id.donate_close);
        imgusl = activity.findViewById(R.id.imgusl);
        plusbc = activity.findViewById(R.id.plusbc);
        plusrub = activity.findViewById(R.id.plusrub);
        moneyy = activity.findViewById(R.id.moneydon);
        donate_down = activity.findViewById(R.id.downnn);
        bcc = activity.findViewById(R.id.bcdon);
        keys = activity.findViewById(R.id.textView9);
        bp = activity.findViewById(R.id.textView10);
        nabori = activity.findViewById(R.id.textView13);
        aks = activity.findViewById(R.id.textView11);
        topusl = activity.findViewById(R.id.toptextuslugi);
        costuscl = activity.findViewById(R.id.costusl);
        tovari = activity.findViewById(R.id.textView5);
        actija = activity.findViewById(R.id.textView33);
        auto = activity.findViewById(R.id.textView15);
        vips = activity.findViewById(R.id.textView12);
        skins = activity.findViewById(R.id.textView14);
        costpodtusl = activity.findViewById(R.id.podt3);
        maintext = activity.findViewById(R.id.textView8);
       // autoo = activity.findViewById(R.id.textView15);
        uslugi = activity.findViewById(R.id.textView7);
        sc_close = activity.findViewById(R.id.closesc);
        sc_close2 = activity.findViewById(R.id.butonok);
        button_okusl = activity.findViewById(R.id.butonokk);
        button_nousl = activity.findViewById(R.id.butonnoo);
        vipsil = activity.findViewById(R.id.donate_button1);
        vipgld = activity.findViewById(R.id.donate_button2);
        vipplt = activity.findViewById(R.id.donate_button3);
        auto_button = activity.findViewById(R.id.donate_buton);
        uslugi_buy = activity.findViewById(R.id.donate_butееon);
        back = activity.findViewById(R.id.imageView22);
        sc1 = activity.findViewById(R.id.sc1);
        sc2 = activity.findViewById(R.id.sc2);
        autoRecycler = activity.findViewById(R.id.autoRecycler);
        uslugiRecycler = activity.findViewById(R.id.UslugiRecycler);
        usl_button1 = activity.findViewById(R.id.button_leftt);
        usl_button2 = activity.findViewById(R.id.button_rightt);
        donate_layout.setVisibility(View.GONE);
        sc_don.setVisibility(View.GONE);
        podt_don.setVisibility(View.GONE);
        setListeners(aactivity);

    }
    public void setListeners(Activity aactivity) {
        activity = aactivity;
        donate_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        plusbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(0);
            }
        });
        plusrub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(1);
            }
        });
        keys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(2);
            }
        });
        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(3);
            }
        });
        nabori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(4);
            }
        });
        skins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                maintext.setText("СКИНЫ");
                auto.setBackgroundColor(Color.parseColor("#00990055"));
                auto.setTextColor(Color.parseColor("#ff666666"));
                vips.setBackgroundColor(Color.parseColor("#00990055"));
                vips.setTextColor(Color.parseColor("#ff666666"));
                actija.setBackgroundColor(Color.parseColor("#00990055"));
                actija.setTextColor(Color.parseColor("#ff666666"));
                skins.setTextColor(Color.parseColor("#ffffff"));
                skins.setBackgroundColor(Color.parseColor("#ffff4500"));
                close_auto();
                close_actions();
                show_skins();
                donate_uslugi.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                donate_vip.setVisibility(View.GONE);
                donate_akijas.setVisibility(View.GONE);
            }
        });
        aks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(5);
            }
        });
        actija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                maintext.setText("АКЦИИ И СПЕЦПРЕДЛОЖЕНИЯ");
                auto.setBackgroundColor(Color.parseColor("#00990055"));
                auto.setTextColor(Color.parseColor("#ff666666"));
                vips.setBackgroundColor(Color.parseColor("#00990055"));
                vips.setTextColor(Color.parseColor("#ff666666"));
                skins.setBackgroundColor(Color.parseColor("#00990055"));
                skins.setTextColor(Color.parseColor("#ff666666"));
                actija.setTextColor(Color.parseColor("#ffffff"));
                actija.setBackgroundColor(Color.parseColor("#ffff4500"));
                close_auto();
                show_actions();
                donate_uslugi.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                donate_vip.setVisibility(View.GONE);
                donate_akijas.setVisibility(View.VISIBLE);
            }
        });
        vips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                maintext.setText("VIP ПАКЕТЫ");
                auto.setBackgroundColor(Color.parseColor("#00990055"));
                auto.setTextColor(Color.parseColor("#ff666666"));
                actija.setBackgroundColor(Color.parseColor("#00990055"));
                actija.setTextColor(Color.parseColor("#ff666666"));
                vips.setTextColor(Color.parseColor("#ffffff"));
                vips.setBackgroundColor(Color.parseColor("#ffff4500"));
                skins.setBackgroundColor(Color.parseColor("#00990055"));
                skins.setTextColor(Color.parseColor("#ff666666"));
                close_auto();
                donate_uslugi.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                donate_akijas.setVisibility(View.GONE);
                donate_vip.setVisibility(View.VISIBLE);
            }
        });
        vipsil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(14);
            }
        });
        vipgld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(15);
            }
        });
        vipplt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                NvEventQueueActivity.getInstance().sendDonateClick(16);
            }
        });
        sc_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_sc();
            }
        });
        sc_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_sc();
            }
        });
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                donate_akijas.setVisibility(View.GONE);
                maintext.setText("ТРАНСПОРТ");
                actija.setBackgroundColor(Color.parseColor("#00990055"));
                actija.setTextColor(Color.parseColor("#ff666666"));
                vips.setBackgroundColor(Color.parseColor("#00990055"));
                vips.setTextColor(Color.parseColor("#ff666666"));
                skins.setBackgroundColor(Color.parseColor("#00990055"));
                skins.setTextColor(Color.parseColor("#ff666666"));
                auto.setTextColor(Color.parseColor("#ffffff"));
                auto.setBackgroundColor(Color.parseColor("#ffff4500"));
                back.setVisibility(View.GONE);
                donate_uslugi.setVisibility(View.GONE);
                donate_vip.setVisibility(View.GONE);
                show_auto();
                if(autooo == 1) {
                    back.setVisibility(View.VISIBLE);
                }
            }
        });
        uslugi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                tovari.setBackgroundColor(Color.parseColor("#00990055"));
                tovari.setTextColor(Color.parseColor("#ff666666"));
                uslugi.setTextColor(Color.parseColor("#ffffff"));
                uslugi.setBackgroundColor(Color.parseColor("#ffff4500"));
                donate_center.setVisibility(View.GONE);
                donate_down.setVisibility(View.GONE);
                maintext.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                donate_uslugi.setVisibility(View.VISIBLE);
                show_uslugi();
                autooo = 0;
            }
        });
        uslugi_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                podt_don.setVisibility(View.VISIBLE);
                costpodtusl.setText(costpodt + " BC?");
            }
        });
        button_nousl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podt_don.setVisibility(View.GONE);
            }
        });
        button_okusl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podt_don.setVisibility(View.GONE);
                if(autooo == 1) {
                        NvEventQueueActivity.getInstance().buycar(carid, carcost,  1);
                }
                else {
                    if(uslugi_status != 0) NvEventQueueActivity.getInstance().sendDonateClick(uslugi_status + 5);
                    else NvEventQueueActivity.getInstance().sendDonateClick(6);
                }

            }
        });
        tovari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(aactivity, R.anim.button_click));
                uslugi.setBackgroundColor(Color.parseColor("#00990055"));
                uslugi.setTextColor(Color.parseColor("#ff666666"));
                tovari.setTextColor(Color.parseColor("#ffffff"));
                tovari.setBackgroundColor(Color.parseColor("#ffff4500"));
                donate_uslugi.setVisibility(View.GONE);
                donate_center.setVisibility(View.VISIBLE);
                donate_down.setVisibility(View.VISIBLE);
                maintext.setVisibility(View.VISIBLE);
                if(autooo == 1) {
                    back.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setVisibility(View.GONE);
                close_auto();
                show_auto();
            }
        });
    }
    public void show(int money, int bc) { // Phương thức để hiển thị màn hình donate.

        donate_layout.setVisibility(View.VISIBLE); // Làm cho layout chính của donate hiển thị.
        this.money = money; // Lưu trữ số tiền được truyền vào.
        this.bc = bc; // Lưu trữ số BC được truyền vào.
        DecimalFormat formatter = new DecimalFormat(); // Tạo đối tượng DecimalFormat để định dạng số.
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(); // Lấy các ký hiệu định dạng mặc định.
        symbols.setGroupingSeparator(' '); // Đặt dấu cách làm dấu phân tách nhóm (ví dụ: 1 000 000).
        formatter.setDecimalFormatSymbols(symbols); // Áp dụng ký hiệu vào formatter.
        String mon = formatter.format(money); // Định dạng số tiền.
        String bcs = formatter.format(bc); // Định dạng số BC.
        moneyy.setText(mon); // Hiển thị số tiền đã định dạng.
        bcc.setText(bcs); // Hiển thị số BC đã định dạng.
        show_actions(); // Hiển thị danh sách các khuyến mãi/hành động.
        //moneyy.setText(new Formatter().format("%d%s", Integer.valueOf(money), "").toString()); // Cách định dạng cũ (đã comment).
        //bcc.setText(new Formatter().format("%d%s", Integer.valueOf(bc), "").toString()); // Cách định dạng cũ (đã comment).
        NvEventQueueActivity.getInstance().togglePlayer(1); // Gửi sự kiện: người chơi đang ở giao diện donate (ví dụ: tạm dừng game).
    }

    public void close() { // Phương thức để đóng màn hình donate.
        donate_layout.setVisibility(View.GONE); // Ẩn layout chính của donate.
        NvEventQueueActivity.getInstance().togglePlayer(0); // Gửi sự kiện: người chơi đã thoát giao diện donate.
    }

    public void update(int money, int bc) { // Phương thức để cập nhật số tiền và BC trên UI.
        this.money = money; // Cập nhật biến tiền.
        this.bc = bc; // Cập nhật biến BC.
        DecimalFormat formatter = new DecimalFormat(); // Tạo đối tượng DecimalFormat.
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(); // Lấy ký hiệu định dạng.
        symbols.setGroupingSeparator(' '); // Đặt dấu cách làm dấu phân tách nhóm.
        formatter.setDecimalFormatSymbols(symbols); // Áp dụng ký hiệu.
        String mon = formatter.format(money); // Định dạng lại số tiền.
        String bcs = formatter.format(bc); // Định dạng lại số BC.
        moneyy.setText(mon); // Hiển thị lại số tiền đã cập nhật.
        bcc.setText(bcs); // Hiển thị lại số BC đã cập nhật.
        //moneyy.setText(new Formatter().format("%d%s", Integer.valueOf(money), "").toString()); // Cách định dạng cũ.
        //bcc.setText(new Formatter().format("%d%s", Integer.valueOf(bc), "").toString()); // Cách định dạng cũ.
    }
    public void show_sc(int money, int bc) { // Phương thức hiển thị màn hình thông báo thành công (Success Screen).
        this.money = money; // Lưu trữ giá trị money (có thể dùng với nhiều mục đích tùy theo giá trị).
        this.bc = bc; // Lưu trữ giá trị bc.
        sc_don.setVisibility(View.VISIBLE); // Hiển thị layout màn hình thành công.
        if(money == -1) { // Trường hợp mua thành công một mặt hàng chung.
            sc1.setText("ВЫ УСПЕШНО ПРИОБРЕЛИ ДАННЫЙ ТОВАР"); // Thông báo: Bạn đã mua thành công sản phẩm này.
            sc2.setText("СПАСИБО ЗА ОПЕРАЦИЮ!"); // Thông báo: Cảm ơn bạn đã giao dịch!
        }
        else if(money == -777) { // Trường hợp mua xe thành công.
            sc1.setText(format("ВЫ УСПЕШНО ПРИОБРЕЛИ %s", getNameAuto())); // Thông báo: Bạn đã mua thành công [Tên Xe].
            sc2.setText("ДЛЯ ТОГО ЧТОБЫ ЗАГРУЗИТЬ АВТО,\nИСПОЛЬЗУЙТЕ /CAR"); // Thông báo: Để tải xe, sử dụng /CAR.
        }
        else if(money == -666) { // Trường hợp mua skin/quần áo thành công.
            sc1.setText("ВЫ УСПЕШНО ПРИОБРЕЛИ НОВУЮ ОДЕЖДУ\nОНА НАДЕТА НА ВАС"); // Thông báo: Bạn đã mua thành công quần áo mới. Nó đã được mặc lên người bạn.
            sc2.setText("ТЕПЕРЬ ВЫ ВЫГЛЯДИТЕ КАК\nНАСТОЯЩИЙ МОДНИК!"); // Thông báo: Bây giờ bạn trông như một tín đồ thời trang thực thụ!
        }
        else { // Trường hợp khác, có thể là chuyển đổi tiền tệ.
            // Thông báo: Bạn đã chuyển [money] BC thành [bc] RUB.
            // Ý nghĩa của money và bc ở đây cần xem xét kỹ hơn trong logic game.
            sc1.setText(new Formatter().format("ВЫ ПЕРЕВЕЛИ %d BC В %d РУБЛЕЙ", Integer.valueOf(money), Integer.valueOf(bc)).toString());
            sc2.setText("СПАСИБО ЗА ОПЕРАЦИЮ!"); // Thông báo: Cảm ơn bạn đã giao dịch!
        }

    }
    public void close_sc() { // Phương thức để đóng màn hình thông báo thành công.
        sc_don.setVisibility(View.GONE); // Ẩn layout màn hình thành công.
    }


    // Phương thức private để thiết lập RecyclerView cho các mục donate (ví dụ: danh mục xe).
    private void setDonateRecycler(List<Donatee> donateList) {
        // Tạo một LayoutManager kiểu LinearLayout, sắp xếp các mục theo chiều ngang (HORIZONTAL).
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (this, RecyclerView.HORIZONTAL, false);

        // Tìm RecyclerView trong layout bằng ID `R.id.autoRecycler` và gán cho biến static `donateRecycler`.
        // Lưu ý: `autoRecycler` là một biến thành viên khác, có thể có sự nhầm lẫn hoặc tái sử dụng ID ở đây.
        donateRecycler = activity.findViewById(R.id.autoRecycler);
        // Đặt LayoutManager cho RecyclerView.
        donateRecycler.setLayoutManager(layoutManager);

        // Tạo một đối tượng DonateAdapter mới với context và danh sách dữ liệu `donateList`.
        donateAdapter = new DonateAdapter(activity, donateList);
        // Đặt Adapter cho RecyclerView.
        donateRecycler.setAdapter(donateAdapter);
    }
    // Phương thức private để thiết lập RecyclerView cho các mục hành động/khuyến mãi.
    private void setActionRecycler(List<Actions> actionsList) {
        // Tạo một LayoutManager kiểu LinearLayout, sắp xếp các mục theo chiều ngang.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (this, RecyclerView.HORIZONTAL, false);

        // Tìm RecyclerView trong layout bằng ID `R.id.ActionsRecycler` và gán cho biến static `actionsRecycler`.
        actionsRecycler = activity.findViewById(R.id.ActionsRecycler);
        // Đặt LayoutManager cho RecyclerView.
        actionsRecycler.setLayoutManager(layoutManager);

        // Tạo một đối tượng ActionsAdapter mới.
        actionsAdapter = new ActionsAdapter(activity, actionsList);
        // Đặt Adapter cho RecyclerView.
        actionsRecycler.setAdapter(actionsAdapter);


    }

    // Phương thức private để thiết lập RecyclerView cho các mục "donat" (có thể là dịch vụ).
    private void setDonatRecycler(List<Donat> donatList) {
        // Tạo một LayoutManager kiểu LinearLayout, sắp xếp các mục theo chiều dọc (VERTICAL).
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager (this, RecyclerView.VERTICAL, false);

        // Tìm RecyclerView trong layout bằng ID `R.id.UslugiRecycler` và gán cho biến static `donatRecycler`.
        // Lưu ý: `UslugiRecycler` là một biến thành viên khác, có thể có sự nhầm lẫn hoặc tái sử dụng ID.
        donatRecycler = activity.findViewById(R.id.UslugiRecycler);
        // Đặt LayoutManager cho RecyclerView.
        donatRecycler.setLayoutManager(layoutManager);

        // Tạo một đối tượng DonatAdapter mới.
        donatAdapter = new DonatAdapter(activity, donatList);
        // Đặt Adapter cho RecyclerView.
        donatRecycler.setAdapter(donatAdapter);
    }
    public void show_uslugi() { // Phương thức hiển thị danh sách các dịch vụ.

        List<Donat> donatList = new ArrayList<>(); // Tạo một ArrayList để chứa các đối tượng Donat (dịch vụ).
        // Thêm các dịch vụ vào danh sách với ID, tên, giá, và tên file ảnh.
        donatList.add(new Donat(1, "ИЗМЕНИТЬ ИМЯ", "50", "change_nickname")); // Đổi tên, 50 BC
        donatList.add(new Donat(2, "СНЯТЬ ВАРН", "100", "delete_warn")); // Xóa cảnh cáo, 100 BC
        donatList.add(new Donat(3, "ВСЕ ЛИЦЕНЗИИ", "150", "licenses")); // Tất cả giấy phép, 150 BC
        donatList.add(new Donat(4, "ПОКУПКА СИЛЫ", "300", "power")); // Mua sức mạnh, 300 BC
        donatList.add(new Donat(5, "САБВУФЕР PIONEER", "1000", "subwoofer")); // Loa siêu trầm Pioneer, 1000 BC
        donatList.add(new Donat(6, "ДОП. СЛОТ НА АВТО", "100", "add_car_slot")); // Thêm chỗ đậu xe, 100 BC
        donatList.add(new Donat(7, "НОМЕРА НА АВТО", "85", "buy_car_number")); // Biển số xe, 85 BC
        donatList.add(new Donat(8, "НОМЕР ТЕЛЕФОНА", "70", "phone_number")); // Số điện thoại, 70 BC
        setDonatRecycler(donatList); // Thiết lập RecyclerView để hiển thị danh sách dịch vụ này.
        uslugiRecycler.setVisibility(View.VISIBLE); // Làm cho RecyclerView dịch vụ hiển thị.
        topusl.setText("ИЗМЕНИТЬ ИМЯ"); // Đặt tên dịch vụ mặc định (đầu tiên trong danh sách) lên TextView.
        costuscl.setText("50 BC"); // Đặt giá dịch vụ mặc định.
        imgusl.setImageResource(R.drawable.change_nickname); // Đặt ảnh dịch vụ mặc định.
        costpodt = "50"; // Lưu giá của dịch vụ mặc định (để xác nhận).
        uslugi_status = 1; // Lưu ID của dịch vụ mặc định.
    }

    // Phương thức cập nhật thông tin chi tiết của dịch vụ được chọn.
    public void upd_usl(String name, String cost, String img, int id) {

        this.uslugi_status = id; // Cập nhật ID dịch vụ đang được chọn.
        this.costpodt = cost; // Cập nhật giá của dịch vụ đang được chọn (để xác nhận).
        topusl.setText(name); // Hiển thị tên dịch vụ được chọn.
        costuscl.setText(cost + " BC"); // Hiển thị giá dịch vụ được chọn.
        // Lấy ID tài nguyên drawable từ tên file ảnh (img) và package name.
        int imgid = activity.getResources().getIdentifier(img, "drawable", activity.getPackageName());
        imgusl.setImageResource(imgid); // Đặt ảnh cho dịch vụ được chọn.
    }
    public void show_auto() { // Phương thức hiển thị các danh mục xe chính.

        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList để chứa các danh mục xe.
        // Thêm các danh mục xe với ID, tên, và tên file ảnh.
        donateList.add(new Donatee(1, "ЭКОНОМ", "", "", "auto_econom", 0)); // Hạng phổ thông
        donateList.add(new Donatee(2, "СРЕДНИЙ", "", "", "auto_middle",0 )); // Hạng trung
        donateList.add(new Donatee(3, "ПРЕМИУМ", "", "", "auto_premium",0 )); // Hạng cao cấp
        donateList.add(new Donatee(4, "МОТО", "", "","auto_moto", 0)); // Xe máy
        donateList.add(new Donatee(5, "УНИКАЛЬНЫЕ", "", "","auto_unique",0 )); // Xe độc đáo
        donateList.add(new Donatee(6, "ГРУЗОВОЙ", "", "","auto_gruz", 0)); // Xe tải
        donateList.add(new Donatee(7, "ЯХТЫ", "", "","auto_yacht", 0)); // Du thuyền
        setDonateRecycler(donateList); // Thiết lập RecyclerView để hiển thị các danh mục này.
        autoRecycler.setVisibility(View.VISIBLE); // Làm cho RecyclerView danh mục xe hiển thị.
    }
    public void close_auto() { // Phương thức đóng/ẩn danh sách xe (danh mục hoặc chi tiết).

        autoRecycler.setVisibility(View.GONE); // Ẩn RecyclerView xe.
        autooo = 0; // Đặt lại `autooo`, có thể để chỉ đã thoát khỏi mục xe hoặc quay về menu chính của xe.
    }
    public void close_actions() { // Phương thức đóng/ẩn danh sách khuyến mãi.

        actionsRecycler.setVisibility(View.GONE); // Ẩn RecyclerView khuyến mãi.
        autooo = 0; // Đặt lại `autooo`.
    }
    public  void show_nizk() { // Phương thức hiển thị danh sách xe hạng thấp ("ЭКОНОМ").

        close_auto(); // Đóng danh sách danh mục xe chính trước.
        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList cho xe hạng thấp.
        // Thêm các mẫu xe hạng thấp với ID, hãng, model, tên file ảnh, và giá.
        donateList.add(new Donatee(8, "ЗАЗ", "968М","auto_zaz", "",10));
        donateList.add(new Donatee(9, "ВАЗ", "1111", "auto_1111","", 15));
        // ... (thêm nhiều xe khác tương tự)
        donateList.add(new Donatee(27, "MERSEDES-BENZ", "E420 W210", "auto_mersw210", "",350));
        setDonateRecycler(donateList); // Hiển thị danh sách xe này.
        autoRecycler.setVisibility(View.VISIBLE); // Làm cho RecyclerView xe hiển thị.
        back.setVisibility(View.VISIBLE); // Hiển thị nút quay lại.
        autooo = 1; // Đặt `autooo` = 1 để chỉ đang ở danh sách xe chi tiết.
    }

    public  void show_sredn() { // Phương thức hiển thị danh sách xe hạng trung ("СРЕДНИЙ").
        close_auto(); // Đóng danh mục xe chính.
        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList cho xe hạng trung.
        // Thêm các mẫu xe hạng trung.
        donateList.add(new Donatee(28, "NISSAN", "Skyline R34","auto_skyline", "",340));
        // ... (thêm nhiều xe khác tương tự)
        donateList.add(new Donatee(45, "VOLVO", "V60 T6", "auto_v60", "",1750));
        setDonateRecycler(donateList); // Hiển thị danh sách.
        autoRecycler.setVisibility(View.VISIBLE); // Hiện RecyclerView.
        back.setVisibility(View.VISIBLE); // Hiện nút quay lại.
        autooo = 1; // Đặt `autooo` = 1.
    }

    public  void show_visok() { // Phương thức hiển thị danh sách xe hạng cao ("ПРЕМИУМ").
        close_auto(); // Đóng danh mục xe chính.
        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList cho xe hạng cao.
        // Thêm các mẫu xe hạng cao.
        donateList.add(new Donatee(46, "VOLVO", "XC90", "auto_xc90", "",2100));
        // ... (thêm nhiều xe khác tương tự)
        donateList.add(new Donatee(70, "LAMBORGINI ", "Aventador S", "auto_lamba", "",10000));
        setDonateRecycler(donateList); // Hiển thị danh sách.
        autoRecycler.setVisibility(View.VISIBLE); // Hiện RecyclerView.
        back.setVisibility(View.VISIBLE); // Hiện nút quay lại.
        autooo = 1; // Đặt `autooo` = 1.
    }

    public  void show_moto() { // Phương thức hiển thị danh sách xe máy ("МОТО").
// donateList.add(new Donatee(11, "", "", "auto_", " BC")); // Dòng này bị comment.
        close_auto(); // Đóng danh mục xe chính.
        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList cho xe máy.
        // Thêm các mẫu xe máy.
        donateList.add(new Donatee(71, "RACER SPORT", "Скутер", "auto_racer", "",10));
        // ... (thêm nhiều xe khác tương tự)
        donateList.add(new Donatee(78, "KAWASAKI", "Ninja H2R", "auto_kawasakininja", "",5000));
        setDonateRecycler(donateList); // Hiển thị danh sách.
        autoRecycler.setVisibility(View.VISIBLE); // Hiện RecyclerView.
        back.setVisibility(View.VISIBLE); // Hiện nút quay lại.
        autooo = 1; // Đặt `autooo` = 1.
    }
    public  void show_skins() { // Phương thức hiển thị danh sách skin.
        close_auto(); // Đóng danh mục xe (nếu có).
        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList cho skin.
        // Thêm skin (hiện tại chỉ có 1).
        donateList.add(new Donatee(83, "ФИРМЕННАЯ\nОДЕЖДА", "Скин администратора", "skin_adm", "",30000)); // Skin admin
        setDonateRecycler(donateList); // Hiển thị danh sách.
        autoRecycler.setVisibility(View.VISIBLE); // Hiện RecyclerView (dùng chung với xe).
        back.setVisibility(View.VISIBLE); // Hiện nút quay lại.
        autooo = 1; // Đặt `autooo` = 1.
    }
    public  void show_uniq() { // Phương thức hiển thị danh sách xe độc đáo ("УНИКАЛЬНЫЕ").
        close_auto(); // Đóng danh mục xe chính.
        List<Donatee> donateList = new ArrayList<>(); // Tạo ArrayList cho xe độc đáo.
        // Thêm các mẫu xe độc đáo (thường là xe đặc biệt, xe công vụ).
        donateList.add(new Donatee(79, "ВАЗ", "2170 (ППС)", "auto_priora", "",20000)); // VAZ 2170 cảnh sát
        // ... (thêm nhiều xe khác tương tự)
        donateList.add(new Donatee(82, "BMW", "M5 F90 (ППС)", "auto_bmwf90", "",50000)); // BMW M5 F90 cảnh sát
        setDonateRecycler(donateList); // Hiển thị danh sách.
        autoRecycler.setVisibility(View.VISIBLE); // Hiện RecyclerView.
        back.setVisibility(View.VISIBLE); // Hiện nút quay lại.
        autooo = 1; // Đặt `autooo` = 1.
    }

    // Phương thức được gọi khi người dùng chọn mua một xe cụ thể, để hiển thị dialog xác nhận.
    public void buy_carpodt(int cost, int id) {
        this.carcost = cost; // Lưu giá xe.
        this.carid = id; // Lưu ID xe.
        autooo = 1; // Đặt trạng thái đang xử lý mua xe.
        podt_don.setVisibility(View.VISIBLE); // Hiển thị dialog xác nhận (`podt_don`).
        costpodtusl.setText(cost + " BC?"); // Hiển thị chi phí xe cần xác nhận.
    }

    // Phương thức để lưu tên xe (được gọi từ adapter khi xe được chọn).
    public void GetCarName(String name) {
        this.CarName = name; // Gán tên xe vào biến thành viên.
    }
    // Phương thức để lấy tên xe đã lưu (dùng cho thông báo mua thành công).
    public String getNameAuto() {
        return CarName; // Trả về tên xe.
    }

    public void show_actions() { // Phương thức hiển thị danh sách các hành động/khuyến mãi.

        List<Actions> actionList = new ArrayList<>(); // Tạo ArrayList cho các khuyến mãi.
        // Thêm các khuyến mãi (hiện tại có 2).
        actionList.add(new Actions(28, "ДЛЯ ВАС", "Nissan", "auto_skyline", 327)); // Cho bạn: Nissan Skyline, giá 327
        actionList.add(new Actions(54, "АКЦИЯ", "Cadilac", "auto_kadilac", 3300)); // Khuyến mãi: Cadilac, giá 3300

        setActionRecycler(actionList); // Thiết lập RecyclerView để hiển thị danh sách này.
        actionsRecycler.setVisibility(View.VISIBLE); // Làm cho RecyclerView khuyến mãi hiển thị.
    }

}