import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscription;

public class HelloWorld {
    public static void main(String[] args) throws Exception {

        Flowable<String> flowable =
                Flowable.create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        String[] dataList = { "Hello, World!", "こんにちは、世界！" };

                        for (String data: dataList) {
                            if (emitter.isCancelled()) {
                                return;
                            }

                            emitter.onNext(data);
                        }
                        emitter.onComplete();
                    }
                }, BackpressureStrategy.BUFFER);

        flowable.observeOn(Schedulers.computation())
                .subscribe(new org.reactivestreams.Subscriber<String>() {
                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        this.subscription.request(1L);
                    }

                    @Override
                    public void onNext(String data) {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + ": " + data);
                        this.subscription.request(1L);
                    }

                    @Override
                    public void onComplete() {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + ": 完了しました");
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });

        Thread.sleep(500L);
    }
}
