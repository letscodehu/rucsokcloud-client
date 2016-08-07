package hu.letscode.cloud.config;

import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import hu.letscode.cloud.model.FileModel;
import hu.letscode.cloud.model.FileModification;


@Configuration
@ComponentScan(basePackages = "hu.letscode.cloud")
public class ApplicationConfig {

	
	@Bean(name = "fileQueue")
	public BlockingQueue<String> fileQueue() {
		return new ArrayBlockingQueue<String>(10);
	}
	
	@Bean(name = "requestQueue")
	public BlockingQueue<FileModification> requestQueue() {
		return new ArrayBlockingQueue<FileModification>(4);
	}
	
	@Value("${watch.directory}")
	private String path;
	
	@Value("${upload.serverUrl}")
	private String serverUrl;
	
	@Bean
	public TrayIcon trayIcon() {
		Image image = new ImageIcon("/home/tacsiazuma/share/java/cloud-client/src/main/resources/images/robin.jpg").getImage();
		TrayIcon trayIcon = new TrayIcon(image);
		trayIcon.setToolTip("Rücsök cloud");
		final JPopupMenu jpopup = popupMenu();
		trayIcon.addMouseListener(new MouseAdapter() {
			

		    @Override
		    public void mousePressed(MouseEvent e) {
		        maybeShowPopup(e);
		    }
		    
		    private void maybeShowPopup(MouseEvent e) {
		    	if (e.isPopupTrigger()) {
                    jpopup.setLocation(e.getX(), e.getY());
                    jpopup.setInvoker(jpopup);
                    jpopup.setVisible(true);
                }
		    }
			
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
        });
		return trayIcon;
	}
	
	@Bean
	public DB dataSource() {
		DB db = DBMaker.fileDB("cloud.db").closeOnJvmShutdown().make();
		return db;
	}
	
	@SuppressWarnings("unchecked")
	@Bean
	public ConcurrentMap<String, FileModel> fileMap() {
		return (ConcurrentMap<String, FileModel>) dataSource().hashMap("files").createOrOpen();
	}
	
	
	
	@Bean
	public JPopupMenu popupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem close = new JMenuItem("Exit");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		popupMenu.add(progressLabel());
		popupMenu.add(close);
		return popupMenu;
	}
	
	public String progressLabel() {
		return "Synchronization in progress... ";
	}


	@Bean
	public Path rootDirectory() {
		return FileSystems.getDefault().getPath(path);
	}
	
	@Bean
	public CloseableHttpClient client() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        // HttpHost host = new HttpHost("localhost",8888, "http");
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                // .setProxy(host)
                .build();
        return httpClient;
	}
	
	
}
