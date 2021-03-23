import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parsing {
    private String address;
    private String home_url;
    private List<List<Article>> list = new ArrayList<>();
    private List<Article> listError = new ArrayList<>();
    private List<String> listURL = new ArrayList<>();
    private int counter;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHome_url() {
        return home_url;
    }

    public void setHome_url(String home_url) {
        this.home_url = home_url;
    }

    public List<List<Article>> getList() {
        return list;
    }

    public void setList(List<List<Article>> list) {
        this.list = list;
    }

    public List<String> getListURL() {
        return listURL;
    }

    public void setListURL(List<String> listURL) {
        this.listURL = listURL;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<Article> getListError() {
        return listError;
    }

    public Parsing(String address) {
        this.address = address;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < address.length(); i++) {
            if (Objects.equals(this.address.charAt(i), '/')) {
                startIndex = i + 2;
                for (int j = i + 2; j < address.length(); j++) {
                    if ((Objects.equals(this.address.charAt(j), '/'))) {
                        endIndex = j--;
                        break;
                    }
                }
                break;
            } else{endIndex = address.length()-1;}
        }
        this.home_url = address.substring(startIndex, endIndex);
    }

    public List<Article> getLinks(String address) throws IOException {
        try {
                Document doc = Jsoup.connect(address).get();
                Elements elementsA = doc.getElementsByTag("a");
                List<Article> listLinks = new ArrayList<>();
                elementsA.forEach(element -> {
                    String url = element.attr("abs:href");
                    if (url.contains(home_url) &&  (!listURL.contains(url))) {
                        listLinks.add(new Article(url));
                        listURL.add(url);
                    }
                });
                list.add(listLinks);
                return listLinks;
        } catch (IOException e ) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int summaryCountPages(List<List<Article>> list) {
        int count = 0;
        int badStatus = getPagesWithIncorretStatus(list);
        for (List<Article> partList : list) {
            count += partList.size();
        }
        return count - badStatus;
    }

    private int getPagesWithIncorretStatus(List<List<Article>> list) {
        int count = 0;
        for (List<Article> partList : list) {
            for (int i = 0; i < partList.size(); i++) {
                if (partList.get(i).getLength() < 0) {
                    count++;
                    listError.add(partList.get(i));
                }
            }
        }
        return count;
    }

    public int summaryWeighPages(List<List<Article>> list) {
        int count = 0;
        for (List<Article> partList : list) {
            for (int i = 0; i < partList.size(); i++) {
                if (partList.get(i).getLength() >= 0) {
                    count += partList.get(i).getLength();
                }
            }
        }
        return count;
    }

    public Article getMinWeighPage(List<List<Article>> list) {
        int min = Integer.MAX_VALUE;
        Article minWeigh = new Article();
        for (List<Article> partList : list) {
            for (int i = 0; i < partList.size(); i++) {
                if (partList.get(i).getLength() < min && partList.get(i).getLength() >= 0) {
                    min = partList.get(i).getLength();
                    minWeigh = partList.get(i);
                }
            }
        }
        return minWeigh;
    }

    public Article getMaxWeighPage(List<List<Article>> list) {
        int max = Integer.MIN_VALUE;
        Article minWeigh = new Article();
        for (List<Article> partList : list) {
            for (Article article : partList) {
                if (article.getLength() > max && article.getLength() >= 0) {
                    max = article.getLength();
                    minWeigh = article;
                }
            }
        }
        return minWeigh;
    }
}

class Article {
    private String url;
    private int length;

    public Article() {
    }

    public Article(String url) {
        this.url = url;
        try {
            Connection.Response response = Jsoup.connect(url).execute();
            this.length = response.bodyAsBytes().length;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            this.length = -1;
        }
    }

    public int getLength() {
        return this.length;
    }

    @Override
    public String toString() {
        return "Article{" +
                "url='" + url + '\'' +
                ", length=" + length +
                '}';
    }

    public String getUrl() {
        return url;
    }

}
