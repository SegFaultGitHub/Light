package Utils;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Visibility {
    public static class Point {
        public float x, y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return Float.compare(point.x, x) == 0 &&
                    Float.compare(point.y, y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    public static class EndPoint {
        public Point point;
        public boolean beginsSegment;
        public Segment segment;
        public float angle;

        public EndPoint(Point point) {
            this.point = point;
        }

        @Override
        public String toString() {
            return "EndPoint{" +
                    "point=" + point +
                    ", beginsSegment=" + beginsSegment +
                    ", segment=" + "N/A" +
                    ", angle=" + angle +
                    '}';
        }
    }
    public static class Segment {
        public EndPoint p1, p2;
        public float d;

        public Segment(Point p1, Point p2) {
            this.p1 = new EndPoint(p1);
            this.p2 = new EndPoint(p2);

            this.p1.segment = this;
            this.p2.segment = this;
        }
    }
    public static class Rectangle {
        public float x, y, width, height;

        public Rectangle(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    public static class LightSource {
        public Point source;
        public List<Point> points;
        public float radius;
        public float currentRadius;
        public Color color;

        public LightSource(Point source, float radius, Color color) {
            this.source = source;
            this.points = new ArrayList<>();
            this.radius = radius;
            this.currentRadius = 0;
            this.color = color;
        }
    }

    // <editor-fold desc="Visibility">
    public static List<Segment> segmentsFromRectangle(Rectangle rectangle) {
        Point nw = new Point(rectangle.x, rectangle.y);
        Point sw = new Point(rectangle.x, rectangle.y + rectangle.height);
        Point ne = new Point(rectangle.x + rectangle.width, rectangle.y);
        Point se = new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
        return Arrays.asList(
                new Segment(nw, ne),
                new Segment(nw, sw),
                new Segment(ne, se),
                new Segment(sw, se)
        );
    }

    private static void calculateEndPointAngles(Point source, Segment segment) {
        float dx = 0.5f * (segment.p1.point.x + segment.p2.point.x) - source.x;
        float dy = 0.5f * (segment.p1.point.y + segment.p2.point.y) - source.y;

        segment.d = (dx * dx) + (dy * dy);
        segment.p1.angle = (float) Math.atan2(segment.p1.point.y - source.y, segment.p1.point.x - source.x);
        segment.p2.angle = (float) Math.atan2(segment.p2.point.y - source.y, segment.p2.point.x - source.x);
    }

    private static void setSegmentBeginning(Segment segment) {
        float dAngle = segment.p2.angle - segment.p1.angle;

        if (dAngle <= -Math.PI) dAngle += 2 * Math.PI;
        if (dAngle > Math.PI) dAngle -= 2 * Math.PI;

        segment.p1.beginsSegment = dAngle > 0;
        segment.p2.beginsSegment = !segment.p1.beginsSegment;
    }

    private static void processSegments(Point source, List<Segment> segments) {
        for (Segment segment : segments) {
            calculateEndPointAngles(source, segment);
            setSegmentBeginning(segment);
        }
    }

    public static List<EndPoint>loadMap(List<Segment> segments, Point source) {
        processSegments(source, segments);
        return segments.stream().flatMap(segment -> Stream.of(segment.p1, segment.p2)).sorted((pointA, pointB) -> {
            if (pointA.angle > pointB.angle) return 1;
            if (pointA.angle < pointB.angle) return -1;
            if (!pointA.beginsSegment && pointB.beginsSegment) return 1;
            if (pointA.beginsSegment && !pointB.beginsSegment) return -1;
            return 0;
        }).collect(Collectors.toList());
    }

    private static boolean leftOf(Segment segment, Point point) {
        float cross = (segment.p2.point.x - segment.p1.point.x) * (point.y - segment.p1.point.y) - (segment.p2.point.y - segment.p1.point.y) * (point.x - segment.p1.point.x);
        return cross < 0;
    }

    private static Point interpolate(Point pointA, Point pointB) {
        return new Point(
                pointA.x * (1 - 0.01f) + pointB.x * 0.01f,
                pointA.y * (1 - 0.01f) + pointB.y * 0.01f
        );
    }

    private static boolean segmentInFrontOf(Segment segmentA, Segment segmentB, Point relativePoint) {
        boolean A1 = leftOf(segmentA, interpolate(segmentB.p1.point, segmentB.p2.point));
        boolean A2 = leftOf(segmentA, interpolate(segmentB.p2.point, segmentB.p1.point));
        boolean A3 = leftOf(segmentA, relativePoint);
        boolean B1 = leftOf(segmentB, interpolate(segmentA.p1.point, segmentA.p2.point));
        boolean B2 = leftOf(segmentB, interpolate(segmentA.p2.point, segmentA.p1.point));
        boolean B3 = leftOf(segmentB, relativePoint);

        if (B1 == B2 && B2 != B3) return true;
        if (A1 == A2 && A2 == A3) return true;
        if (A1 == A2 && A2 != A3) return false;
        if (B1 == B2 && B2 == B3) return false;

        return false;
    }

    private static Point lineIntersection(Point point1, Point point2, Point point3, Point point4) {
        float s = (
                (point4.x - point3.x) * (point1.y - point3.y) -
                        (point4.y - point3.y) * (point1.x - point3.x)
        ) / (
                (point4.y - point3.y) * (point2.x - point1.x) -
                        (point4.x - point3.x) * (point2.y - point1.y)
        );

        return new Point(
                point1.x + s * (point2.x - point1.x),
                point1.y + s * (point2.y - point1.y)
        );
    }

    private static List<Point> getTrianglePoints(Point source, float angle1, float angle2, Segment segment) {
        Point p1 = source;
        Point p2 = new Point(source.x + (float) Math.cos(angle1), source.y + (float) Math.sin(angle1));
        Point p3 = new Point(0, 0);
        Point p4 = new Point(0, 0);

        if (segment != null) {
            p3.x = segment.p1.point.x;
            p3.y = segment.p1.point.y;
            p4.x = segment.p2.point.x;
            p4.y = segment.p2.point.y;
        } else {
            p3.x = source.x + (float) Math.cos(angle1) * 200;
            p3.y = source.y + (float) Math.sin(angle1) * 200;
            p4.x = source.x + (float) Math.cos(angle2) * 200;
            p4.y = source.y + (float) Math.sin(angle2) * 200;
        }

        Point pBegin = lineIntersection(p3, p4, p1, p2);

        p2.x = source.x + (float) Math.cos(angle2);
        p2.y = source.y + (float) Math.sin(angle2);

        Point pEnd = lineIntersection(p3, p4, p1, p2);

        return Arrays.asList(pBegin, pEnd);
    }

    public static void calculateVisibility(LightSource lightSource, List<EndPoint> endPoints) {
        List<Segment> openSegments = new ArrayList<>();
        lightSource.points.clear();
        float beginAngle = 0;

        for (int pass = 0; pass < 2; pass++) {
            for (EndPoint endPoint : endPoints) {
                Segment openSegment = getOrNull(openSegments, 0);

                if (endPoint.beginsSegment) {
                    int index = 0;
                    Segment segment = getOrNull(openSegments, index);
                    while (segment != null && segmentInFrontOf(endPoint.segment, segment, lightSource.source)) {
                        index++;
                        segment = getOrNull(openSegments, index);
                    }

                    if (segment == null) {
                        openSegments.add(endPoint.segment);
                    } else {
                        openSegments.add(index, endPoint.segment);
                    }
                } else {
                    int index = openSegments.indexOf(endPoint.segment);
                    if (index != -1) openSegments.remove(index);
                }

                if (openSegment != getOrNull(openSegments, 0)) {
                    if (pass == 1) {
                        List<Point> _points = getTrianglePoints(lightSource.source, beginAngle, endPoint.angle, openSegment);
                        lightSource.points.addAll(_points);
                    }
                    beginAngle = endPoint.angle;
                }
            }
        }
    }
    // </editor-fold>

    public static void drawFieldOfView(
            RenderTexture partialShadeTexture, RenderTexture completeShadeTexture,
            LightSource lightSource, Color color
    ) {
        partialShadeTexture.clear(Color.BLACK);
        Vertex[] vertices = new Vertex[2 + lightSource.points.size()];
        vertices[0] = new Vertex(new Vector2f(
                lightSource.source.x,
                lightSource.source.y
        ), color);
        if (!lightSource.points.isEmpty()) {
            for (int i = 0; i <= lightSource.points.size(); i++) {
                Point point = lightSource.points.get(i % lightSource.points.size());
                vertices[i + 1] = new Vertex(new Vector2f(
                        point.x,
                        point.y
                ), color);
            }
        }
        partialShadeTexture.draw(vertices, PrimitiveType.TRIANGLE_FAN);
        partialShadeTexture.display();
        completeShadeTexture.draw(new Sprite(partialShadeTexture.getTexture()), new RenderStates(BlendMode.ADD));
    }

    public static void drawRangeView(
            RenderTexture partialShadeTexture, RenderTexture completeShadeTexture,
            LightSource lightSource
    ) {
        partialShadeTexture.clear(Color.BLACK);
        int pointCount = 64;
        Vertex[] vertices = new Vertex[2 + pointCount];
        vertices[0] = new Vertex(new Vector2f(
                lightSource.source.x,
                lightSource.source.y
        ), Color.WHITE);
        double angleStep = 2 * Math.PI / (pointCount - 1);
        double angle = 0;
        for (int i = 1; i <= pointCount; i++) {
            vertices[i] = new Vertex(
                    new Vector2f(
                            (float) (lightSource.source.x + Math.sin(angle) * lightSource.currentRadius),
                            (float) (lightSource.source.y + Math.cos(angle) * lightSource.currentRadius)
                    ), Color.BLACK
            );
            angle += angleStep;
        }
        vertices[1 + pointCount] = vertices[0];
        partialShadeTexture.draw(vertices, PrimitiveType.TRIANGLE_FAN);
        partialShadeTexture.display();
        completeShadeTexture.draw(new Sprite(partialShadeTexture.getTexture()), new RenderStates(BlendMode.MULTIPLY));
    }

    private static <T> T getOrNull(List<T> list, int index) {
        return (index >= list.size() || index < 0) ? null : list.get(index);
    }

    public static Point intersection(Point from, Point to, List<Segment> segments) {
        float t = segments.stream()
                .map(segment -> {
                    Pair<Float, Float> pair = segmentIntersection(from, to, segment.p1.point, segment.p2.point);
                    if (pair != null) return pair.second;
                    return 1f;
                })
                .min(Comparable::compareTo)
                .orElse(1f);
        return new Point(
                (to.x - from.x) * t + from.x,
                (to.y - from.y) * t + from.y
        );
    }

    private static Pair<Float, Float> segmentIntersection(Point p1, Point p2, Point p3, Point p4) {
        float s1_x = p2.x - p1.x;
        float s1_y = p2.y - p1.y;
        float s2_x = p4.x - p3.x;
        float s2_y = p4.y - p3.y;

        float s, t;
        s = (-s1_y * (p1.x - p3.x) + s1_x * (p1.y - p3.y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = ( s2_x * (p1.y - p3.y) - s2_y * (p1.x - p3.x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            return new Pair<>(s, t);
        }

        return null;
    }
}
